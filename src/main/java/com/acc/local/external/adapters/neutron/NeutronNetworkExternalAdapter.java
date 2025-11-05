package com.acc.local.external.adapters.neutron;

import com.acc.global.common.PageResponse;
import com.acc.global.exception.network.NetworkErrorCode;
import com.acc.global.exception.network.NetworkException;
import com.acc.global.exception.network.NeutronErrorCode;
import com.acc.global.exception.network.NeutronException;
import com.acc.local.dto.network.ViewNetworksResponse;
import com.acc.local.external.dto.neutron.networks.CreateNetworkRequest;
import com.acc.local.external.modules.neutron.NeutronNetworksAPIModule;
import com.acc.local.external.modules.neutron.NeutronSubnetsAPIModule;
import com.acc.local.external.ports.NeutronNetworkExternalPort;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClientException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class NeutronNetworkExternalAdapter implements NeutronNetworkExternalPort {

    private final NeutronNetworksAPIModule networksAPIModule;
    private final NeutronSubnetsAPIModule subnetsAPIModule;

    @Override
    public String callCreateGeneralNetwork(String keystoneToken, String name, String description, int mtu) {
        ResponseEntity<JsonNode> response;
        try {
            response = networksAPIModule.createNeutronNetwork(keystoneToken,
                    CreateNetworkRequest.builder().
                            network(
                                    CreateNetworkRequest.Network.builder().
                                            name(name).
                                            description(description).
                                            mtu(mtu).
                                            build()
                            ).
                            build());
        } catch (WebClientException e) {
            throw new NeutronException(NeutronErrorCode.NEUTRON_NETWORK_CREATION_FAILED);
        }

        if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
            throw new NeutronException(NeutronErrorCode.NEUTRON_NETWORK_CREATION_FAILED);
        }

        return response.getBody().
                get("network").
                get("id").
                asText();
    }

    @Override
    public void callDeleteNetwork(String keystoneToken, String networkId) {

        ResponseEntity<JsonNode> response;
        try {
            response = networksAPIModule.deleteNeutronNetwork(keystoneToken, networkId);
        } catch (WebClientException e) {
            throw new NeutronException(NeutronErrorCode.NEUTRON_NETWORK_DELETION_FAILED);
        }

        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new NeutronException(NeutronErrorCode.NEUTRON_NETWORK_DELETION_FAILED);
        }
    }

    @Override
    public PageResponse<ViewNetworksResponse> callListNetworks(String keystoneToken, String projectId, String marker, String direction, int limit) {

        ResponseEntity<JsonNode> response;
        try {
            response = networksAPIModule.listNeutronNetworks(keystoneToken,
                    getListNetworksParams(projectId, marker, direction,  limit == 0 ? 0 : limit + 1));
        } catch (WebClientException e) {
            throw new NeutronException(NeutronErrorCode.NEUTRON_NETWORK_RETRIEVAL_FAILED);
        }

        if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
            throw new NeutronException(NeutronErrorCode.NEUTRON_NETWORK_RETRIEVAL_FAILED);
        }

        List<ViewNetworksResponse> networks = parseNetworks(keystoneToken, response);
        return getNetworksPageResponse(marker, limit, networks);
    }

    @Override
    public Map<String, String> getNetworkNameAndId(String keystoneToken, String networkId) {

        ResponseEntity<JsonNode> response;

        try {
            response = networksAPIModule.showNeutronNetwork(keystoneToken, networkId);
        } catch (WebClientException e) {
            if (e.getMessage().contains("404")) {
                throw new NetworkException(NetworkErrorCode.NOT_FOUND_NETWORK);
            } else {
                throw new NeutronException(NeutronErrorCode.NEUTRON_NETWORK_RETRIEVAL_FAILED);
            }
        }

        if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
            throw new NeutronException(NeutronErrorCode.NEUTRON_NETWORK_RETRIEVAL_FAILED);
        }

        return parseNetworkNameAndId(response);
    }

    private List<ViewNetworksResponse.Subnet> callListSubnetsByNetworkId(String keystoneToken, String networkId) {
        ResponseEntity<JsonNode> response;

        try {
            response = subnetsAPIModule.listSubnets(keystoneToken,
                    Map.of("network_id", networkId));
        } catch (WebClientException e) {
            throw new NeutronException(NeutronErrorCode.NEUTRON_SUBNET_RETRIEVAL_FAILED);
        }
        if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
            throw new NeutronException(NeutronErrorCode.NEUTRON_SUBNET_RETRIEVAL_FAILED);
        }

        List<ViewNetworksResponse.Subnet> subnets = new ArrayList<>();
        for (JsonNode node : response.getBody().get("subnets")) {
            subnets.add(ViewNetworksResponse.Subnet.builder()
                    .subnetId(node.get("id").asText())
                    .subnetName(node.get("name").asText())
                    .cidr(node.get("cidr").asText())
                    .build());
        }

        return subnets; 
    }

    private Map<String, String> getListNetworksParams(String projectId, String marker, String direction, int limit) {
        Map<String, String> params = new HashMap<>(Map.of(
                "project_id", projectId,
                "page_reverse", direction.equals("prev") ? "true" : "false",
                "limit", String.valueOf(limit)
        ));

        if (marker != null && !marker.isEmpty()) {
            params.put("marker", marker);
        }

        return params;
    }

    private PageResponse<ViewNetworksResponse> getNetworksPageResponse(String marker, int limit, List<ViewNetworksResponse> networks) {
        int returnedSize = networks.size();
        if (limit != 0 && returnedSize == limit + 1) {
            networks.removeLast();
        }

        return PageResponse.<ViewNetworksResponse>builder()
                .contents(networks)
                .nextMarker(limit == 0 || returnedSize <= limit ? null : networks.getLast().getNetworkId())
                .prevMarker(limit == 0 || marker == null ? null : networks.getFirst().getNetworkId())
                .last(limit == 0 || returnedSize <= limit)
                .first(marker == null || limit == 0)
                .size(networks.size())
                .build();
    }

    private List<ViewNetworksResponse> parseNetworks(String keystoneToken, ResponseEntity<JsonNode> response) {
        List<ViewNetworksResponse> networks = new ArrayList<>();
        for ( JsonNode node : response.getBody().get("networks")) {
            networks.add(ViewNetworksResponse.builder()
                    .networkId(node.get("id").asText())
                    .networkName(node.get("name").asText())
                    .subnets(callListSubnetsByNetworkId(keystoneToken, node.get("id").asText()))
                    .build());
        }
        return networks;
    }

    private Map<String, String> parseNetworkNameAndId(ResponseEntity<JsonNode> response) {
        JsonNode networkNode = response.getBody().get("network");
        Map<String, String> result = new HashMap<>();
        result.put("id", networkNode.get("id").asText());
        result.put("name", networkNode.get("name").asText());
        return result;
    }
}
