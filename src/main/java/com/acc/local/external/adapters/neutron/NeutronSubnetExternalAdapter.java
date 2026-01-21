package com.acc.local.external.adapters.neutron;

import com.acc.global.common.PageResponse;
import com.acc.global.exception.network.NeutronErrorCode;
import com.acc.global.exception.network.NeutronException;
import com.acc.local.dto.network.CreateSubnetRequest;
import com.acc.local.dto.network.ViewSubnetsResponse;
import com.acc.local.external.dto.neutron.subnets.BulkCreateSubnetRequest;
import com.acc.local.external.modules.neutron.NeutronSubnetsAPIModule;
import com.acc.local.external.ports.NeutronSubnetExternalPort;
import com.acc.local.external.dto.neutron.response.NeutronSubnetsResponse;
import com.acc.local.external.dto.neutron.response.NeutronSubnetResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
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
            ResponseEntity<NeutronSubnetsResponse> response = subnetsAPIModule.bulkCreateSubnets(keystoneToken,
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
            for (NeutronSubnetsResponse.Subnet node : response.getBody().getSubnets()) {
                sub.add(Map.of(
                        "id", node.getId(),
                        "name", node.getName()
                ));
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
            ResponseEntity<Void> response = subnetsAPIModule.deleteSubnet(keystoneToken, subnetId);

            if (response == null || !response.getStatusCode().is2xxSuccessful()) {
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
            ResponseEntity<NeutronSubnetsResponse> response = subnetsAPIModule.listSubnets(keystoneToken,
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

            ResponseEntity<NeutronSubnetResponse> response = subnetsAPIModule.showSubnet(keystoneToken, subnetId);

            if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
                throw new NeutronException(NeutronErrorCode.NEUTRON_SUBNET_RETRIEVAL_FAILED);
            }

            var body = response.getBody().getSubnet();
            return ViewSubnetsResponse.builder()
                    .subnetId(body.getId())
                    .subnetName(body.getName())
                    .networkId(body.getNetworkId())
                    .cidr(body.getCidr())
                    .gatewayIp(body.getGatewayIp())
                    .createdAt(body.getCreatedAt())
                    .description(body.getDescription())
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

    private List<ViewSubnetsResponse> parseSubnets(ResponseEntity<NeutronSubnetsResponse> response) {
        List<ViewSubnetsResponse> subnets = new ArrayList<>();
        for (NeutronSubnetsResponse.Subnet node : response.getBody().getSubnets()) {
            subnets.add(
                    ViewSubnetsResponse.builder()
                            .subnetId(node.getId())
                            .subnetName(node.getName())
                            .networkId(node.getNetworkId())
                            .cidr(node.getCidr())
                            .gatewayIp(node.getGatewayIp())
                            .createdAt(node.getCreatedAt())
                            .description(node.getDescription())
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
