package com.acc.local.external.adapters.neutron;

import com.acc.global.common.PageResponse;
import com.acc.global.exception.network.NetworkErrorCode;
import com.acc.global.exception.network.NetworkException;
import com.acc.global.exception.network.NeutronErrorCode;
import com.acc.global.exception.network.NeutronException;
import com.acc.local.dto.network.ViewNetworksResponse;
import com.acc.local.external.dto.neutron.networks.CreateNetworkRequest;
import com.acc.local.external.dto.neutron.response.NeutronNetworksResponse;
import com.acc.local.external.dto.neutron.response.NeutronNetworkResponse;
import com.acc.local.external.modules.neutron.NeutronNetworksAPIModule;
import com.acc.local.external.modules.neutron.NeutronSubnetsAPIModule;
import com.acc.local.external.ports.NeutronNetworkExternalPort;
import com.acc.local.external.dto.neutron.response.NeutronNetworksResponse;
import com.acc.local.external.dto.neutron.response.NeutronNetworkResponse;
import com.acc.local.external.dto.neutron.response.NeutronFloatingIpsResponse;
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
            ResponseEntity<NeutronNetworkResponse> response = networksAPIModule.createNeutronNetwork(keystoneToken,
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

            return response.getBody().getNetwork().getId();
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
            ResponseEntity<Void> response = networksAPIModule.deleteNeutronNetwork(keystoneToken, networkId);

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
            ResponseEntity<NeutronNetworksResponse> response = networksAPIModule.listNeutronNetworks(keystoneToken,
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
            ResponseEntity<NeutronNetworkResponse> response = networksAPIModule.showNeutronNetwork(keystoneToken, networkId);

            if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
                throw new NeutronException(NeutronErrorCode.NEUTRON_NETWORK_RETRIEVAL_FAILED);
            }

            var network = response.getBody().getNetwork();
            return Map.of(
                    "id", network.getId(),
                    "name", network.getName()
            );
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
            ResponseEntity<NeutronNetworksResponse> response = networksAPIModule.listNeutronNetworks(keystoneToken,
                    Map.of("router:external", "true"));

            if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
                throw new NeutronException(NeutronErrorCode.NEUTRON_NETWORK_RETRIEVAL_FAILED);
            }

            var net = response.getBody().getNetworks().get(0);
            return Map.of(
                    "id", net.getId(),
                    "name", net.getName()
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
            ResponseEntity<NeutronNetworksResponse> response = networksAPIModule.listNeutronNetworks(
                    keystoneToken,
                    Map.of("project_id", projectId,
                            "name", networkName,
                            "fields", "id")
            );

            if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
                throw new NeutronException(NeutronErrorCode.NEUTRON_NETWORK_RETRIEVAL_FAILED);
            }

            List<String> networkIds = new ArrayList<>();
            for (NeutronNetworksResponse.Network node : response.getBody().getNetworks()) {
                networkIds.add(node.getId());
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
            ResponseEntity<com.acc.local.external.dto.neutron.response.NeutronSubnetsResponse> response = subnetsAPIModule.listSubnets(keystoneToken,
                    Map.of("network_id", networkId));
            if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
                throw new NeutronException(NeutronErrorCode.NEUTRON_SUBNET_RETRIEVAL_FAILED);
            }

            List<ViewNetworksResponse.Subnet> subnets = new ArrayList<>();
            for (com.acc.local.external.dto.neutron.response.NeutronSubnetsResponse.Subnet node : response.getBody().getSubnets()) {
                subnets.add(ViewNetworksResponse.Subnet.builder()
                        .subnetId(node.getId())
                        .subnetName(node.getName())
                        .cidr(node.getCidr())
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

    private List<ViewNetworksResponse> parseNetworks(String keystoneToken, ResponseEntity<NeutronNetworksResponse> response) {
        List<ViewNetworksResponse> networks = new ArrayList<>();
        for ( NeutronNetworksResponse.Network node : response.getBody().getNetworks()) {
            networks.add(ViewNetworksResponse.builder()
                    .networkId(node.getId())
                    .networkName(node.getName())
                    .subnets(callListSubnetsByNetworkId(keystoneToken, node.getId()))
                    .build());
        }
        return networks;
    }
    
}
