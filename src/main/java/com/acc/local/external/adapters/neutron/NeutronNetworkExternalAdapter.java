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
public class NeutronNetworkExternalAdapter implements NeutronNetworkExternalPort {

    private final NeutronNetworksAPIModule networksAPIModule;
    private final NeutronSubnetsAPIModule subnetsAPIModule;

    @Override
    public String callCreateGeneralNetwork(String keystoneToken, String name, String description, int mtu) {
        try {
            ResponseEntity<JsonNode> response = networksAPIModule.createNeutronNetwork(keystoneToken,
                    CreateNetworkRequest.builder().
                            network(
                                    CreateNetworkRequest.Network.builder().
                                            name(name).
                                            description(description).
                                            mtu(mtu).
                                            build()
                            ).
                            build());

            if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
                throw new NeutronException(NeutronErrorCode.NEUTRON_NETWORK_CREATION_FAILED);
            }

            return response.getBody().
                    get("network").
                    get("id").
                    asText();
        } catch (WebClientResponseException e) {
            log.error(e.getMessage(), e.getResponseBodyAsString(), e);
            switch (e.getStatusCode().value()) {
                case 400 -> throw new NeutronException(NeutronErrorCode.NEUTRON_NETWORK_BAD_REQUEST, e);
                case 403 -> throw new NeutronException(NeutronErrorCode.NEUTRON_NETWORK_FORBIDDEN, e);
                default -> throw new NeutronException(NeutronErrorCode.NEUTRON_NETWORK_CREATION_FAILED, e);
            }
        } 
    }

    @Override
    public void callDeleteNetwork(String keystoneToken, String networkId) {
        try {
            ResponseEntity<JsonNode> response = networksAPIModule.deleteNeutronNetwork(keystoneToken, networkId);

            if (!response.getStatusCode().is2xxSuccessful()) {
                throw new NeutronException(NeutronErrorCode.NEUTRON_NETWORK_DELETION_FAILED);
            }
        } catch (WebClientResponseException e) {
            log.error(e.getMessage(), e.getResponseBodyAsString(), e);
            switch (e.getStatusCode().value()) {
                case 400 -> throw new NeutronException(NeutronErrorCode.NEUTRON_NETWORK_BAD_REQUEST, e);
                case 403 -> throw new NeutronException(NeutronErrorCode.NEUTRON_NETWORK_FORBIDDEN, e);
                case 404 -> throw new NeutronException(NeutronErrorCode.NEUTRON_NETWORK_NOT_FOUND, e);
                default -> throw new NeutronException(NeutronErrorCode.NEUTRON_NETWORK_DELETION_FAILED, e);
            }
        }
    }

    @Override
    public PageResponse<ViewNetworksResponse> callListNetworks(String keystoneToken, String projectId, String marker, String direction, int limit) {
        try {
            ResponseEntity<JsonNode> response = networksAPIModule.listNeutronNetworks(keystoneToken,
                    getListNetworksParams(projectId, marker, direction,  limit == 0 ? 0 : limit + 1));

            if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
                throw new NeutronException(NeutronErrorCode.NEUTRON_NETWORK_RETRIEVAL_FAILED);
            }

            List<ViewNetworksResponse> networks = parseNetworks(keystoneToken, response);
            return getNetworksPageResponse(marker, limit, networks);
        } catch (WebClientResponseException e) {
            log.error(e.getMessage(), e.getResponseBodyAsString(), e);
            switch (e.getStatusCode().value()) {
                case 400 -> throw new NeutronException(NeutronErrorCode.NEUTRON_NETWORK_BAD_REQUEST, e);
                case 403 -> throw new NeutronException(NeutronErrorCode.NEUTRON_NETWORK_FORBIDDEN, e);
                default -> throw new NeutronException(NeutronErrorCode.NEUTRON_NETWORK_RETRIEVAL_FAILED, e);
            }
        } 
    }

    @Override
    public Map<String, String> getNetworkNameAndId(String keystoneToken, String networkId) {
        try {
            ResponseEntity<JsonNode> response = networksAPIModule.showNeutronNetwork(keystoneToken, networkId);

            if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
                throw new NeutronException(NeutronErrorCode.NEUTRON_NETWORK_RETRIEVAL_FAILED);
            }

            return parseNetworkNameAndId(response);
        } catch (WebClientResponseException e) {
            log.error(e.getMessage(), e.getResponseBodyAsString(), e);
            switch (e.getStatusCode().value()) {
                case 400 -> throw new NeutronException(NeutronErrorCode.NEUTRON_NETWORK_BAD_REQUEST, e);
                case 403 -> throw new NeutronException(NeutronErrorCode.NEUTRON_NETWORK_FORBIDDEN, e);
                case 404 -> throw new NetworkException(NetworkErrorCode.NOT_FOUND_NETWORK, e);
                default -> throw new NeutronException(NeutronErrorCode.NEUTRON_NETWORK_RETRIEVAL_FAILED, e);
            }
        }
    }

    @Override
    public Map<String, String> getProviderNetwork(String keystoneToken) {
        try {
            ResponseEntity<JsonNode> response = networksAPIModule.listNeutronNetworks(keystoneToken,
                    Map.of("router:external", "true"));

            if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
                throw new NeutronException(NeutronErrorCode.NEUTRON_NETWORK_RETRIEVAL_FAILED);
            }

            return Map.of(
                    "id", response.getBody().get("networks").get(0).get("id").asText(),
                    "name", response.getBody().get("networks").get(0).get("name").asText()
            );
        } catch (WebClientResponseException e) {
            log.error(e.getMessage(), e.getResponseBodyAsString(), e);
            switch (e.getStatusCode().value()) {
                case 400 -> throw new NeutronException(NeutronErrorCode.NEUTRON_NETWORK_BAD_REQUEST, e);
                case 403 -> throw new NeutronException(NeutronErrorCode.NEUTRON_NETWORK_FORBIDDEN, e);
                default -> throw new NeutronException(NeutronErrorCode.NEUTRON_NETWORK_RETRIEVAL_FAILED, e);
            }
        }
    }

    @Override
    public List<String> callListNetworksByNetworkName(String keystoneToken, String projectId, String networkName) {
        try {
            ResponseEntity<JsonNode> response = networksAPIModule.listNeutronNetworks(
                    keystoneToken,
                    Map.of("project_id", projectId,
                            "name", networkName,
                            "fields", "id")
            );

            if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
                throw new NeutronException(NeutronErrorCode.NEUTRON_NETWORK_RETRIEVAL_FAILED);
            }

            List<String> networkIds = new ArrayList<>();
            for (JsonNode node : response.getBody().get("networks")) {
                networkIds.add(node.get("id").asText());
            }

            return networkIds;
        } catch (WebClientResponseException e) {
            log.error(e.getMessage(), e.getResponseBodyAsString(), e);
            switch (e.getStatusCode().value()) {
                case 400 -> throw new NeutronException(NeutronErrorCode.NEUTRON_NETWORK_BAD_REQUEST, e);
                case 403 -> throw new NeutronException(NeutronErrorCode.NEUTRON_NETWORK_FORBIDDEN, e);
                default -> throw new NeutronException(NeutronErrorCode.NEUTRON_NETWORK_RETRIEVAL_FAILED, e);
            }
        }
    }

    private List<ViewNetworksResponse.Subnet> callListSubnetsByNetworkId(String keystoneToken, String networkId) {
        try {
            ResponseEntity<JsonNode> response = subnetsAPIModule.listSubnets(keystoneToken,
                    Map.of("network_id", networkId));
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
        } catch (WebClientResponseException e) {
            log.error(e.getMessage(), e.getResponseBodyAsString(), e);
            switch (e.getStatusCode().value()) {
                case 400 -> throw new NeutronException(NeutronErrorCode.NEUTRON_SUBNET_BAD_REQUEST, e);
                case 403 -> throw new NeutronException(NeutronErrorCode.NEUTRON_SUBNET_FORBIDDEN, e);
                default -> throw new NeutronException(NeutronErrorCode.NEUTRON_SUBNET_RETRIEVAL_FAILED, e);
            }
        }
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
