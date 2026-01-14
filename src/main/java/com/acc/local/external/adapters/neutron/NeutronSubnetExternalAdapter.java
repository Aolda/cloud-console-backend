package com.acc.local.external.adapters.neutron;

import com.acc.global.common.PageResponse;
import com.acc.global.exception.network.NeutronErrorCode;
import com.acc.global.exception.network.NeutronException;
import com.acc.local.dto.network.CreateNetworkRequest;
import com.acc.local.dto.network.CreateSubnetRequest;
import com.acc.local.dto.network.ViewSubnetsResponse;
import com.acc.local.external.dto.neutron.subnets.BulkCreateSubnetRequest;
import com.acc.local.external.modules.neutron.NeutronSubnetsAPIModule;
import com.acc.local.external.ports.NeutronSubnetExternalPort;
import com.fasterxml.jackson.databind.JsonNode;
import io.swagger.v3.core.util.Json;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClientException;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class NeutronSubnetExternalAdapter implements NeutronSubnetExternalPort {

    private final NeutronSubnetsAPIModule subnetsAPIModule;

    public List<Map<String, String>> callCreateSubnet(String keystoneToken, List<CreateSubnetRequest> subnets, String networkId) {
        try {
            ResponseEntity<JsonNode> response = subnetsAPIModule.bulkCreateSubnets(keystoneToken,
                    BulkCreateSubnetRequest.builder()
                            .subnets(
                                    subnets.stream().map(subnet -> BulkCreateSubnetRequest.Subnet.builder()
                                            .cidr(subnet.getCidr())
                                            .networkId(networkId)
                                            .ipVersion(4)
                                            .description(subnet.getDescription())
                                            .gatewayIp(subnet.getGatewayIp())
                                            .name(subnet.getSubnetName())
                                            .build()).toList()
                            )
                            .build()
            );

            if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
                throw new NeutronException(NeutronErrorCode.NEUTRON_SUBNET_CREATION_FAILED);
            }

            List<Map<String, String>> sub = new ArrayList<>();
            for (JsonNode node : response.getBody().get("subnets")) {
                sub.add(
                        Map.of("id", node.get("id").asText(),
                                "name", node.get("name").asText())
                );
            }
            return sub;
        } catch (WebClientResponseException e) {
            switch (e.getStatusCode().value()) {
                case 400 -> throw new NeutronException(NeutronErrorCode.NEUTRON_SUBNET_BAD_REQUEST, e);
                case 403 -> throw new NeutronException(NeutronErrorCode.NEUTRON_SUBNET_FORBIDDEN, e);
                case 404 -> throw new NeutronException(NeutronErrorCode.NEUTRON_SUBNET_NOT_FOUND, e);
                default -> throw new NeutronException(NeutronErrorCode.NEUTRON_SUBNET_CREATION_FAILED, e);
            }
        }
    }

    @Override
    public void callDeleteSubnet(String keystoneToken, String subnetId) {
        try {
            ResponseEntity<JsonNode> response = subnetsAPIModule.deleteSubnet(keystoneToken, subnetId);

            if (!response.getStatusCode().is2xxSuccessful()) {
                throw new NeutronException(NeutronErrorCode.NEUTRON_SUBNET_DELETION_FAILED);
            }
        } catch (WebClientResponseException e) {
            switch (e.getStatusCode().value()) {
                case 400 -> throw new NeutronException(NeutronErrorCode.NEUTRON_SUBNET_BAD_REQUEST, e);
                case 403 -> throw new NeutronException(NeutronErrorCode.NEUTRON_SUBNET_FORBIDDEN, e);
                case 404 -> throw new NeutronException(NeutronErrorCode.NEUTRON_SUBNET_NOT_FOUND, e);
                default -> throw new NeutronException(NeutronErrorCode.NEUTRON_SUBNET_DELETION_FAILED, e);
            }
        }
    }

    @Override
    public PageResponse<ViewSubnetsResponse> callListSubnets(String keystoneToken, String networkId, String marker, String direction, int limit) {
        try {
            ResponseEntity<JsonNode> response = subnetsAPIModule.listSubnets(keystoneToken,
                    getListSubnetsParams(networkId, marker, direction, limit > 0 ? limit + 1 : 0));

            if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
                throw new NeutronException(NeutronErrorCode.NEUTRON_SUBNET_RETRIEVAL_FAILED);
            }

            List<ViewSubnetsResponse> subnets = parseSubnets(response);
            return getSubnetsPageResponse(marker, limit, subnets);
        } catch (WebClientResponseException e) {
            switch (e.getStatusCode().value()) {
                case 400 -> throw new NeutronException(NeutronErrorCode.NEUTRON_SUBNET_BAD_REQUEST, e);
                case 403 -> throw new NeutronException(NeutronErrorCode.NEUTRON_SUBNET_FORBIDDEN, e);
                case 404 -> throw new NeutronException(NeutronErrorCode.NEUTRON_SUBNET_NOT_FOUND, e);
                default -> throw new NeutronException(NeutronErrorCode.NEUTRON_SUBNET_RETRIEVAL_FAILED, e);
            }
        }
    }

    @Override
    public ViewSubnetsResponse getSubnetDetails(String keystoneToken, String subnetId) {
        try {

            ResponseEntity<JsonNode> response = subnetsAPIModule.showSubnet(keystoneToken, subnetId);

            if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
                throw new NeutronException(NeutronErrorCode.NEUTRON_SUBNET_RETRIEVAL_FAILED);
            }

            JsonNode body = response.getBody().get("subnet");
            return ViewSubnetsResponse.builder()
                    .subnetId(body.get("id").asText())
                    .subnetName(body.get("name").asText())
                    .networkId(body.get("network_id").asText())
                    .cidr(body.get("cidr").asText())
                    .gatewayIp(body.get("gateway_ip").asText())
                    .createdAt(body.get("created_at").asText())
                    .description(body.get("description").asText())
                    .build();
        } catch (WebClientResponseException e) {
            switch (e.getStatusCode().value()) {
                case 400 -> throw new NeutronException(NeutronErrorCode.NEUTRON_SUBNET_BAD_REQUEST, e);
                case 401 -> throw new NeutronException(NeutronErrorCode.NEUTRON_SUBNET_UNAUTHORIZED, e);
                case 404 -> throw new NeutronException(NeutronErrorCode.NEUTRON_SUBNET_NOT_FOUND, e);
                default -> throw new NeutronException(NeutronErrorCode.NEUTRON_SUBNET_RETRIEVAL_FAILED, e);
            }
        }

    }

    private Map<String, String> getListSubnetsParams(String networkId, String marker, String direction, int limit) {
        Map<String, String> params = new HashMap<>();
        params.put("network_id", networkId);
        params.put("page_reverse", direction.equals("prev") ? "true" : "false");
        params.put("limit", String.valueOf(limit));

        if (marker != null && !marker.isEmpty()) {
            params.put("marker", marker);
        }

        return params;
    }

    private List<ViewSubnetsResponse> parseSubnets(ResponseEntity<JsonNode> response) {
        List<ViewSubnetsResponse> subnets = new ArrayList<>();
        for (JsonNode node : response.getBody().get("subnets")) {
            subnets.add(
                    ViewSubnetsResponse.builder()
                            .subnetId(node.get("id").asText())
                            .subnetName(node.get("name").asText())
                            .networkId(node.get("network_id").asText())
                            .cidr(node.get("cidr").asText())
                            .gatewayIp(node.get("gateway_ip").asText())
                            .createdAt(node.get("created_at").asText())
                            .description(node.get("description").asText())
                            .build()
            );
        }
        return subnets;
    }

    private PageResponse<ViewSubnetsResponse> getSubnetsPageResponse(String marker, int limit, List<ViewSubnetsResponse> subnets) {
        int returnedSize = subnets.size();
        if (limit != 0 && returnedSize == limit + 1) {
            subnets.removeLast();
        }
        return PageResponse.<ViewSubnetsResponse>builder()
                .contents(subnets)
                .nextMarker(limit == 0 || returnedSize <= limit ? null : subnets.getLast().getSubnetId())
                .prevMarker(marker == null ? null : subnets.getFirst().getSubnetId())
                .last(limit == 0 || returnedSize <= limit)
                .first(marker == null || limit == 0)
                .size(subnets.size())
                .build();
    }
}
