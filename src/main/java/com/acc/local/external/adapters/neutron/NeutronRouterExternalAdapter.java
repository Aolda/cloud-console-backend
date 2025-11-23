package com.acc.local.external.adapters.neutron;

import com.acc.global.common.PageResponse;
import com.acc.global.exception.network.NetworkErrorCode;
import com.acc.global.exception.network.NetworkException;
import com.acc.global.exception.network.NeutronErrorCode;
import com.acc.global.exception.network.NeutronException;
import com.acc.local.dto.network.ViewRoutersResponse;
import com.acc.local.external.dto.neutron.common.ExternalGatewayInfo;
import com.acc.local.external.dto.neutron.routers.AddRouterInterfaceRequest;
import com.acc.local.external.dto.neutron.routers.CreateRouterRequest;
import com.acc.local.external.modules.neutron.NeutronRoutersAPIModule;
import com.acc.local.external.ports.NeutronRouterExternalPort;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClientException;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class NeutronRouterExternalAdapter implements NeutronRouterExternalPort {

    private final NeutronRoutersAPIModule routersAPIModule;

    @Override
    public PageResponse<ViewRoutersResponse> callListRouters(String keystoneToken, String projectId, String marker, String direction, int limit) {
        ResponseEntity<JsonNode> response;

        try {
            response = routersAPIModule.listRouters(keystoneToken,
                    getListRoutersParams(projectId, marker, direction, limit > 0 ? limit + 1 : 0));
        } catch (WebClientException e) {
            throw new NeutronException(NeutronErrorCode.NEUTRON_ROUTER_RETRIEVAL_FAILED);
        }

        if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
            throw new NeutronException(NeutronErrorCode.NEUTRON_ROUTER_RETRIEVAL_FAILED);
        }

        List<ViewRoutersResponse> routers = parseRouters(response);

        return getRoutersPageResponse(marker, limit, routers);
    }

    @Override
    public void callDeleteRouter(String keystoneToken, String routerId) {
        ResponseEntity<JsonNode> response;
        try {
            response = routersAPIModule.deleteRouter(keystoneToken, routerId);

        } catch (WebClientException e) {
            if (e.getMessage().contains("404")) {
                throw new NetworkException(NetworkErrorCode.NOT_FOUND_ROUTER);
            }
            throw new NeutronException(NeutronErrorCode.NEUTRON_ROUTER_DELETION_FAILED);
        }
        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new NeutronException(NeutronErrorCode.NEUTRON_ROUTER_DELETION_FAILED);
        }
    }

    @Override
    public String callCreateRouter(String keystoneToken, String routerName, String networkId) {
        ResponseEntity<JsonNode> response;
        try {
            response = routersAPIModule.createRouter(keystoneToken,
                    CreateRouterRequest.builder().router(
                            CreateRouterRequest.Router.builder()
                                    .name(routerName)
                                    .externalGatewayInfo(
                                            networkId != null ?
                                            ExternalGatewayInfo.builder()
                                                    .networkId(networkId)
                                                    .enableSnat(true)
                                                    .build() : null
                                    )
                                    .build()
                    ).build(
                    ));
        } catch (WebClientException e) {
            throw new NeutronException(NeutronErrorCode.NEUTRON_ROUTER_CREATION_FAILED, e);
        }

        if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
            throw new NeutronException(NeutronErrorCode.NEUTRON_ROUTER_CREATION_FAILED);
        }

        return response.getBody().get("router").get("id").asText();
    }

    @Override
    public String callAddRouterInterface(String keystoneToken, String routerId, String subnetId) {
        try {
            ResponseEntity<JsonNode> response = routersAPIModule.addRouterInterface(
                keystoneToken,
                routerId,
                AddRouterInterfaceRequest.builder()
                        .subnetId(subnetId)
                        .build()
            );

            if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
                throw new NeutronException(NeutronErrorCode.NEUTRON_ROUTER_CREATION_FAILED);
            }

            return response.getBody().get("id").asText();
        } catch (WebClientResponseException e) {
            throw new NeutronException(NeutronErrorCode.NEUTRON_ROUTER_CONNECT_SUBNET_FAILED);
        }
    }

    @Override
    public Map<String, String> getRouterNameAndId(String keystoneToken, String routerId) {
        try {
            ResponseEntity<JsonNode> response = routersAPIModule.showRouter(
                    keystoneToken,
                    routerId
            );

            if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
                throw new NeutronException(NeutronErrorCode.NEUTRON_ROUTER_RETRIEVAL_FAILED);
            }

            JsonNode router = response.getBody().get("router");
            return Map.of(
                    "id", routerId,
                    "name", router.get("name").asText()
            );
        } catch (WebClientResponseException e) {
            throw new NeutronException(NeutronErrorCode.NEUTRON_ROUTER_RETRIEVAL_FAILED);
        }

    }

    private List<ViewRoutersResponse> parseRouters(ResponseEntity<JsonNode> response) {
        List<ViewRoutersResponse> routers = new ArrayList<>();
        for (JsonNode routerNode : response.getBody().get("routers")) {
            boolean isGateway = routerNode.hasNonNull("external_gateway_info");

            routers.add(ViewRoutersResponse.builder()
                    .routerId(routerNode.get("id").asText())
                    .routerName(routerNode.get("name").asText())
                    .status(routerNode.get("status").asText())
                    .isExternal(isGateway)
                    .externalIp(isGateway ? routerNode.get("external_gateway_info").get("external_fixed_ips").get(0).get("ip_address").asText() : null)
                    .createdAt(routerNode.get("created_at").asText())
                    .build());
        }

        return routers;
    }

    private MultiValueMap<String, String> getListRoutersParams(String projectId, String marker, String direction, int limit) {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("project_id", projectId);
        params.add("page_reverse", direction.equals("prev") ? "true" : "false");
        params.add("limit", String.valueOf(limit));

        params.add("fields", "id");
        params.add("fields", "name");
        params.add("fields", "status");
        params.add("fields", "external_gateway_info");
        params.add("fields", "description");
        params.add("fields", "created_at");

        if (marker != null && !marker.isEmpty()) {
            params.add("marker", marker);
        }

        return params;
    }

    private PageResponse<ViewRoutersResponse> getRoutersPageResponse(String marker, int limit, List<ViewRoutersResponse> routers) {
        int returnedSize = routers.size();
        if (limit != 0 && returnedSize == limit + 1) {
            routers.removeLast();
        }

        return PageResponse.<ViewRoutersResponse>builder()
                .contents(routers)
                .nextMarker(limit == 0 || returnedSize <= limit ? null : routers.getLast().getRouterId())
                .prevMarker(marker == null ? null : routers.getFirst().getRouterId())
                .last(limit == 0 || returnedSize <= limit)
                .first(marker == null || limit == 0)
                .size(routers.size())
                .build();
    }
}
