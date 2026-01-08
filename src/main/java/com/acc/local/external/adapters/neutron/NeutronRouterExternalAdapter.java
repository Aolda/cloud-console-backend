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
import com.acc.local.external.dto.neutron.routers.RemoveRouterInterfaceRequest;
import com.acc.local.external.modules.neutron.NeutronRoutersAPIModule;
import com.acc.local.external.ports.NeutronRouterExternalPort;
import com.acc.local.external.dto.neutron.response.NeutronRoutersResponse;
import com.acc.local.external.dto.neutron.response.NeutronRouterResponse;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class NeutronRouterExternalAdapter implements NeutronRouterExternalPort {

    private final NeutronRoutersAPIModule routersAPIModule;

    @Override
    public PageResponse<ViewRoutersResponse> callListRouters(String keystoneToken, String projectId, String marker, String direction, int limit) {
        try {
            ResponseEntity<NeutronRoutersResponse> response = routersAPIModule.listRouters(keystoneToken,
                    getListRoutersParams(projectId, marker, direction, limit > 0 ? limit + 1 : 0));

            if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
                throw new NeutronException(NeutronErrorCode.NEUTRON_ROUTER_RETRIEVAL_FAILED);
            }

            List<ViewRoutersResponse> routers = parseRouters(response);
            return getRoutersPageResponse(marker, limit, routers);
        } catch (WebClientResponseException e) {
            log.error(e.getMessage(), e.getResponseBodyAsString(), e);
            switch (e.getStatusCode().value()) {
                case 400 -> throw new NeutronException(NeutronErrorCode.NEUTRON_ROUTER_BAD_REQUEST, e);
                case 403 -> throw new NeutronException(NeutronErrorCode.NEUTRON_ROUTER_FORBIDDEN, e);
                default -> throw new NeutronException(NeutronErrorCode.NEUTRON_ROUTER_RETRIEVAL_FAILED, e);
            }
        }
    }

    @Override
    public void callDeleteRouter(String keystoneToken, String routerId) {
        try {
            ResponseEntity<JsonNode> response = routersAPIModule.deleteRouter(keystoneToken, routerId);

            if (!response.getStatusCode().is2xxSuccessful()) {
                throw new NeutronException(NeutronErrorCode.NEUTRON_ROUTER_DELETION_FAILED);
            }
        } catch (WebClientResponseException e) {
            log.error(e.getMessage(), e.getResponseBodyAsString(), e);
            switch (e.getStatusCode().value()) {
                case 400 -> throw new NeutronException(NeutronErrorCode.NEUTRON_ROUTER_BAD_REQUEST, e);
                case 403 -> throw new NeutronException(NeutronErrorCode.NEUTRON_ROUTER_FORBIDDEN, e);
                case 404 -> throw new NetworkException(NetworkErrorCode.NOT_FOUND_ROUTER, e);
                default -> throw new NeutronException(NeutronErrorCode.NEUTRON_ROUTER_DELETION_FAILED, e);
            }
        }
    }

    @Override
    public String callCreateRouter(String keystoneToken, String routerName, String networkId) {
        try {
            ResponseEntity<NeutronRouterResponse> response = routersAPIModule.createRouter(keystoneToken,
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

            if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
                throw new NeutronException(NeutronErrorCode.NEUTRON_ROUTER_CREATION_FAILED);
            }

            return response.getBody().getRouter().getId();
        } catch (WebClientResponseException e) {
            log.error(e.getMessage(), e.getResponseBodyAsString(), e);
            switch (e.getStatusCode().value()) {
                case 400 -> throw new NeutronException(NeutronErrorCode.NEUTRON_ROUTER_BAD_REQUEST, e);
                case 403 -> throw new NeutronException(NeutronErrorCode.NEUTRON_ROUTER_FORBIDDEN, e);
                default -> throw new NeutronException(NeutronErrorCode.NEUTRON_ROUTER_CREATION_FAILED, e);
            }
        }
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
                throw new NeutronException(NeutronErrorCode.NEUTRON_ROUTER_CONNECT_SUBNET_FAILED);
            }

            return response.getBody().get("id").asText();
        } catch (WebClientResponseException e) {
            log.error(e.getMessage(), e.getResponseBodyAsString(), e);
            switch (e.getStatusCode().value()) {
                case 400 -> throw new NeutronException(NeutronErrorCode.NEUTRON_ROUTER_BAD_REQUEST, e);
                case 403 -> throw new NeutronException(NeutronErrorCode.NEUTRON_ROUTER_FORBIDDEN, e);
                case 404 -> throw new NeutronException(NeutronErrorCode.NEUTRON_ROUTER_NOT_FOUND, e);
                case 409 -> throw new NeutronException(NeutronErrorCode.NEUTRON_ROUTER_CONFLICT, e);
                default -> throw new NeutronException(NeutronErrorCode.NEUTRON_ROUTER_CONNECT_SUBNET_FAILED, e);
            }
        }
    }

    @Override
    public void callAddRouterInterfaceByPortId(String keystoneToken, String routerId, String portId) {
        try {
            ResponseEntity<JsonNode> response = routersAPIModule.addRouterInterface(
                keystoneToken,
                routerId,
                AddRouterInterfaceRequest.builder()
                        .portId(portId)
                        .build()
            );

            if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
                throw new NeutronException(NeutronErrorCode.NEUTRON_ROUTER_CONNECT_SUBNET_FAILED);
            }
        } catch (WebClientResponseException e) {
            switch (e.getStatusCode().value()) {
                case 400 -> throw new NeutronException(NeutronErrorCode.NEUTRON_ROUTER_BAD_REQUEST, e);
                case 403 -> throw new NeutronException(NeutronErrorCode.NEUTRON_ROUTER_FORBIDDEN, e);
                case 404 -> throw new NeutronException(NeutronErrorCode.NEUTRON_ROUTER_NOT_FOUND, e);
                default -> throw new NeutronException(NeutronErrorCode.NEUTRON_ROUTER_CONNECT_SUBNET_FAILED, e);
            }
        }
    }


    public void callRemoveRouterInterface(String keystoneToken, String routerId, String subnetId) {
        try {
            ResponseEntity<JsonNode> response = routersAPIModule.removeRouterInterface(
                keystoneToken,
                routerId,
                    RemoveRouterInterfaceRequest.builder()
                        .subnetId(subnetId)
                        .build()
            );

            if (!response.getStatusCode().is2xxSuccessful()) {
                throw new NeutronException(NeutronErrorCode.NEUTRON_ROUTER_DISCONNECT_SUBNET_FAILED);
            }
        } catch (WebClientResponseException e) {
            switch (e.getStatusCode().value()) {
                case 400 -> throw new NeutronException(NeutronErrorCode.NEUTRON_ROUTER_BAD_REQUEST, e);
                case 403 -> throw new NeutronException(NeutronErrorCode.NEUTRON_ROUTER_FORBIDDEN, e);
                case 404 -> throw new NeutronException(NeutronErrorCode.NEUTRON_ROUTER_NOT_FOUND, e);
                default -> throw new NeutronException(NeutronErrorCode.NEUTRON_ROUTER_DISCONNECT_SUBNET_FAILED, e);
            }
        }
    }

    @Override
    public Map<String, String> getRouterNameAndId(String keystoneToken, String routerId) {
        try {
            ResponseEntity<NeutronRouterResponse> response = routersAPIModule.showRouter(
                    keystoneToken,
                    routerId
            );

            if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
                throw new NeutronException(NeutronErrorCode.NEUTRON_ROUTER_RETRIEVAL_FAILED);
            }

            var router = response.getBody().getRouter();
            return Map.of(
                    "id", routerId,
                    "name", router.getName()
            );
        } catch (WebClientResponseException e) {
            log.error(e.getMessage(), e.getResponseBodyAsString(), e);
            switch (e.getStatusCode().value()) {
                case 400 -> throw new NeutronException(NeutronErrorCode.NEUTRON_ROUTER_BAD_REQUEST, e);
                case 403 -> throw new NeutronException(NeutronErrorCode.NEUTRON_ROUTER_FORBIDDEN, e);
                case 404 -> throw new NetworkException(NetworkErrorCode.NOT_FOUND_ROUTER, e);
                default -> throw new NeutronException(NeutronErrorCode.NEUTRON_ROUTER_RETRIEVAL_FAILED, e);
            }
        }

    }

    private List<ViewRoutersResponse> parseRouters(ResponseEntity<NeutronRoutersResponse> response) {
        List<ViewRoutersResponse> routers = new ArrayList<>();
        for (NeutronRoutersResponse.Router routerNode : response.getBody().getRouters()) {
            boolean isGateway = routerNode.getExternalGatewayInfo() != null;

            routers.add(ViewRoutersResponse.builder()
                    .routerId(routerNode.getId())
                    .routerName(routerNode.getName())
                    .status(routerNode.getStatus())
                    .isExternal(isGateway)
                    .externalIp(isGateway && routerNode.getExternalGatewayInfo().getExternalFixedIps() != null
                            && !routerNode.getExternalGatewayInfo().getExternalFixedIps().isEmpty()
                            ? routerNode.getExternalGatewayInfo().getExternalFixedIps().get(0).getIpAddress()
                            : null)
                    .createdAt(routerNode.getCreatedAt())
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
