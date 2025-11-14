package com.acc.local.external.adapters.neutron;

import com.acc.global.common.PageResponse;
import com.acc.global.exception.network.NetworkErrorCode;
import com.acc.global.exception.network.NetworkException;
import com.acc.global.exception.network.NeutronErrorCode;
import com.acc.global.exception.network.NeutronException;
import com.acc.local.dto.network.ViewInterfacesResponse;
import com.acc.local.external.dto.neutron.ports.CreatePortRequest;
import com.acc.local.external.modules.neutron.NeutronFloatingIpsAPIModule;
import com.acc.local.external.modules.neutron.NeutronNetworksAPIModule;
import com.acc.local.external.modules.neutron.NeutronPortsAPIModule;
import com.acc.local.external.modules.nova.NovaServerAPIModule;
import com.acc.local.external.ports.NeutronPortExternalPort;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class NeutronPortExternalAdapter implements NeutronPortExternalPort {

    private final NeutronPortsAPIModule portsAPIModule;
    private final NeutronNetworksAPIModule networksAPIModule;
    private final NeutronFloatingIpsAPIModule floatingIpsAPIModule;
    private final NovaServerAPIModule serverAPIModule;

    @Override
    public Map<String, String> callCreatePort(String keystoneToken,
                                              String networkId,
                                              String portName,
                                              String subnetId,
                                              List<String> securityGroupIds,
                                              String description) {

        try {
            ResponseEntity<JsonNode> response = portsAPIModule.createPort( keystoneToken,
                    CreatePortRequest.builder().port(
                            CreatePortRequest.Port.builder()
                                    .name(portName)
                                    .networkId(networkId)
                                    .fixedIps(
                                            subnetId != null ? List.of(
                                                    CreatePortRequest.FixedIp.builder()
                                                            .subnetId(subnetId)
                                                            .build()
                                            ) : null
                                    )
                                    .securityGroups(securityGroupIds)
                                    .portSecurityEnabled(true)
                                    .description(description)
                                    .build()
                    ).build());

            if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
                throw new NeutronException(NeutronErrorCode.NEUTRON_PORT_CREATION_FAILED);
            }

            JsonNode portNode = response.getBody().get("port");
            return Map.of(
                    "id", portNode.get("id").asText()
            );

        } catch (WebClientException e) {
            throw new NeutronException(NeutronErrorCode.NEUTRON_PORT_CREATION_FAILED);
        }
    }

    @Override
    public void callDeletePort(String keystoneToken, String portId) {
        try {
            ResponseEntity<JsonNode> response = portsAPIModule.deletePort(keystoneToken, portId);

            if (!response.getStatusCode().is2xxSuccessful()) {
                throw new NeutronException(NeutronErrorCode.NEUTRON_PORT_DELETION_FAILED);
            }
        } catch (WebClientException e) {
            throw new NeutronException(NeutronErrorCode.NEUTRON_PORT_DELETION_FAILED);
        }
    }

    @Override
    public PageResponse<ViewInterfacesResponse> callListPorts(String keystoneToken,
                                                              String projectId,
                                                              String marker,
                                                              String direction,
                                                              int limit,
                                                              String deviceId,
                                                              String networkId) {
        ResponseEntity<JsonNode> response;
        try {
            response = portsAPIModule.listPorts(keystoneToken,
                    getListPortsParams(projectId, marker, direction, limit > 0 ? limit + 1 : 0, deviceId, networkId));
        } catch (WebClientException e) {
            throw new NeutronException(NeutronErrorCode.NEUTRON_PORT_RETRIEVAL_FAILED);
        }

        if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
            throw new NeutronException(NeutronErrorCode.NEUTRON_PORT_RETRIEVAL_FAILED);
        }

        List<ViewInterfacesResponse> ports = parseInterfaces(keystoneToken, response);
        return getInterfacesPageResponse(marker, limit, ports);
    }

    @Override
    public Map<String, String> getPortInfo(String keystoneToken, String portId) {
        try {
            ResponseEntity<JsonNode> response = portsAPIModule.showPort(keystoneToken, portId);

            if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
                throw new NeutronException(NeutronErrorCode.NEUTRON_PORT_RETRIEVAL_FAILED);
            }

            JsonNode portNode = response.getBody().get("port");

            return Map.of(
                    "id", portNode.get("id").asText(),
                    "name", portNode.get("name").asText()
            );
        } catch (WebClientException e) {
            if (e.getMessage().contains("404")) {
                throw new NetworkException(NetworkErrorCode.NOT_FOUND_INTERFACE);
            }
            throw new NeutronException(NeutronErrorCode.NEUTRON_PORT_RETRIEVAL_FAILED);
        }
    }

    private MultiValueMap<String, String> getListPortsParams(String projectId, String marker, String direction, int limit, String deviceId, String networkId) {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("project_id", projectId);
        params.add("page_reverse", direction.equals("prev") ? "true" : "false");
        params.add("limit", String.valueOf(limit));
        params.add("device_owner", "compute:nova");
        params.add("device_owner", "");

        params.add("fields", "id");
        params.add("fields", "name");
        params.add("fields", "status");
        params.add("fields", "fixed_ips");
        params.add("fields", "device_id");
        params.add("fields", "network_id");
        params.add("fields", "mac_address");

        if (marker != null && !marker.isEmpty()) {
            params.add("marker", marker);
        }
        if (deviceId != null && !deviceId.isEmpty()) {
            params.add("device_id", deviceId);
        }
        if (networkId != null && !networkId.isEmpty()) {
            params.add("network_id", networkId);
        }
        return params;
    }

    private List<ViewInterfacesResponse> parseInterfaces(String keystoneToken, ResponseEntity<JsonNode> response) {
        List<ViewInterfacesResponse> ports = new ArrayList<>();
        for (JsonNode portsNode : response.getBody().get("ports")) {

            boolean hasNetwork = portsNode.hasNonNull("network_id") && !portsNode.get("network_id").asText().isEmpty();
            boolean hasDevice = portsNode.hasNonNull("device_id") && !portsNode.get("device_id").asText().isEmpty();
            String serverName = hasDevice ? getServerName(keystoneToken, portsNode.get("device_id").asText()) : null;
            String networkName = hasNetwork ? getNetworkName(keystoneToken, portsNode.get("network_id").asText()) : null;

            ports.add(ViewInterfacesResponse.builder()
                    .interfaceId(portsNode.get("id").asText())
                    .interfaceName(portsNode.get("name").asText())
                    .status(portsNode.get("status").asText())
                    .mac(portsNode.get("mac_address").asText())
                    .instance(hasDevice ? ViewInterfacesResponse.Instance.builder()
                                    .instanceId(portsNode.get("device_id").asText())
                                    .instanceName(serverName)
                                    .build() : null)
                    .network(hasNetwork ? ViewInterfacesResponse.Network.builder()
                                    .networkId(portsNode.get("network_id").asText())
                                    .networkName(networkName)
                                    .build() : null)
                    .internalIp(portsNode.hasNonNull("fixed_ips") && !portsNode.get("fixed_ips").isEmpty() ?
                            portsNode.get("fixed_ips").get(0).get("ip_address").asText() : null)
                    .externalIp(getFloatingIP(keystoneToken, portsNode.get("id").asText()))
                    .build());
        }

        return ports;
    }

    private String getServerName(String keystoneToken, String serverId) {
        try {
            ResponseEntity<JsonNode> response = serverAPIModule.showServer(keystoneToken, serverId);

            if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
                throw new NeutronException(NeutronErrorCode.NEUTRON_PORT_RETRIEVAL_FAILED);
                // TODO: 인스턴스 예외 코드로 바꾸기
            }

            return response.getBody().get("server").get("name").asText();
        } catch (WebClientException e) {
            throw new NeutronException(NeutronErrorCode.NEUTRON_PORT_RETRIEVAL_FAILED);
            // TODO: 인스턴스 예외 코드로 바꾸기
        }
    }

    private String getNetworkName(String keystoneToken, String networkId) {
        try {
            ResponseEntity<JsonNode> response = networksAPIModule.showNeutronNetwork(keystoneToken, networkId);

            if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
                throw new NeutronException(NeutronErrorCode.NEUTRON_NETWORK_RETRIEVAL_FAILED);
            }
            return response.getBody().get("network").get("name").asText();
        } catch (WebClientResponseException e) {
            if (e.getStatusCode().value() == 404) {
                throw new NetworkException(NetworkErrorCode.NOT_FOUND_NETWORK);
            }
            throw new NeutronException(NeutronErrorCode.NEUTRON_NETWORK_RETRIEVAL_FAILED);
        }
    }

    private String getFloatingIP(String keystoneToken, String portId) {
        try {
            ResponseEntity<JsonNode> response = floatingIpsAPIModule.listFloatingIps(keystoneToken,
                    Map.of("port_id", portId));

            if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
                throw new NeutronException(NeutronErrorCode.NEUTRON_FLOATING_IP_RETRIEVAL_FAILED);
            }

            if (response.getBody().get("floatingips").isEmpty()) {
                return null;
            }

            JsonNode floatingIpNode = response.getBody().get("floatingips").get(0);
            return floatingIpNode.get("floating_ip_address").asText();
        } catch (WebClientResponseException e) {
            log.error(e.getMessage(), e.getResponseBodyAsString(), e);
            throw new NeutronException(NeutronErrorCode.NEUTRON_FLOATING_IP_RETRIEVAL_FAILED);
        }
    }

    private PageResponse<ViewInterfacesResponse> getInterfacesPageResponse(String marker, int limit, List<ViewInterfacesResponse> interfaces) {
        int returnedSize = interfaces.size();
        if (limit != 0 && returnedSize == limit + 1) {
            interfaces.removeLast();
        }

        return PageResponse.<ViewInterfacesResponse>builder()
                .contents(interfaces)
                .nextMarker(limit == 0 || returnedSize <= limit ? null : interfaces.getLast().getInterfaceId())
                .prevMarker(marker == null ? null : interfaces.getFirst().getInterfaceId())
                .last(limit == 0 || returnedSize <= limit)
                .first(marker == null || limit == 0)
                .size(interfaces.size())
                .build();
    }
}
