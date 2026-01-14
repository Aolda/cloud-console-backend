package com.acc.local.external.adapters.neutron;

import com.acc.global.common.PageResponse;
import com.acc.global.exception.instance.NovaErrorCode;
import com.acc.global.exception.instance.NovaException;
import com.acc.global.exception.network.NetworkErrorCode;
import com.acc.global.exception.network.NetworkException;
import com.acc.global.exception.network.NeutronErrorCode;
import com.acc.global.exception.network.NeutronException;
import com.acc.local.domain.enums.network.InterfaceStatus;
import com.acc.local.dto.network.ViewInterfacesResponse;
import com.acc.local.external.dto.neutron.ports.CreatePortRequest;
import com.acc.local.external.dto.neutron.response.NeutronPortsResponse;
import com.acc.local.external.dto.neutron.response.NeutronPortResponse;
import com.acc.local.external.dto.neutron.response.NeutronFloatingIpsResponse;
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

        return callCreatePort(keystoneToken, networkId, portName, subnetId, securityGroupIds, description, true);
    }

    @Override
    public Map<String, String> callCreatePort(String keystoneToken, String networkId, String portName, String subnetId, List<String> securityGroupIds, String description, boolean portSecurity) {
        try {
            ResponseEntity<NeutronPortResponse> response = portsAPIModule.createPort( keystoneToken,
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
                                    .portSecurityEnabled(portSecurity)
                                    .description(description)
                                    .build()
                    ).build());

            if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
                throw new NeutronException(NeutronErrorCode.NEUTRON_PORT_CREATION_FAILED);
            }

            return Map.of(
                    "id", response.getBody().getPort().getId()
            );

        } catch (WebClientResponseException e) {
            log.error(e.getMessage(), e.getResponseBodyAsString(), e);
            switch (e.getStatusCode().value()) {
                case 400 -> throw new NeutronException(NeutronErrorCode.NEUTRON_PORT_BAD_REQUEST, e);
                case 403 -> throw new NeutronException(NeutronErrorCode.NEUTRON_PORT_FORBIDDEN, e);
                case 404 -> throw new NeutronException(NeutronErrorCode.NEUTRON_PORT_NETWORK_RESOURCE_NOT_FOUND, e);
                default -> throw new NeutronException(NeutronErrorCode.NEUTRON_PORT_CREATION_FAILED, e);
            }
        }
    }

    @Override
    public void callDeletePort(String keystoneToken, String portId) {
        try {
            ResponseEntity<JsonNode> response = portsAPIModule.deletePort(keystoneToken, portId);

            if (!response.getStatusCode().is2xxSuccessful()) {
                throw new NeutronException(NeutronErrorCode.NEUTRON_PORT_DELETION_FAILED);
            }
        } catch (WebClientResponseException e) {
            log.error(e.getMessage(), e.getResponseBodyAsString(), e);
            switch (e.getStatusCode().value()) {
                case 400 -> throw new NeutronException(NeutronErrorCode.NEUTRON_PORT_BAD_REQUEST, e);
                case 403 -> throw new NeutronException(NeutronErrorCode.NEUTRON_PORT_FORBIDDEN, e);
                case 404 -> throw new NetworkException(NetworkErrorCode.NOT_FOUND_INTERFACE, e);
                default -> throw new NeutronException(NeutronErrorCode.NEUTRON_PORT_DELETION_FAILED, e);
            }
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
        ResponseEntity<NeutronPortsResponse> response;
        try {
            response = portsAPIModule.listPorts(keystoneToken,
                    getListPortsParams(projectId, marker, direction, limit > 0 ? limit + 1 : 0, deviceId, networkId));
        } catch (WebClientResponseException e) {
            log.error(e.getMessage(), e.getResponseBodyAsString(), e);
            switch (e.getStatusCode().value()) {
                case 400 -> throw new NeutronException(NeutronErrorCode.NEUTRON_PORT_BAD_REQUEST, e);
                case 403 -> throw new NeutronException(NeutronErrorCode.NEUTRON_PORT_FORBIDDEN, e);
                default -> throw new NeutronException(NeutronErrorCode.NEUTRON_PORT_RETRIEVAL_FAILED, e);
            }
        }

        if (response == null || !response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
            throw new NeutronException(NeutronErrorCode.NEUTRON_PORT_RETRIEVAL_FAILED);
        }

        List<ViewInterfacesResponse> ports = parseInterfaces(keystoneToken, response);
        return getInterfacesPageResponse(marker, limit, ports);
    }

    @Override
    public Map<String, String> getPortInfo(String keystoneToken, String portId) {
        try {
            ResponseEntity<NeutronPortResponse> response = portsAPIModule.showPort(keystoneToken, portId);

            if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
                throw new NeutronException(NeutronErrorCode.NEUTRON_PORT_RETRIEVAL_FAILED);
            }

            var portNode = response.getBody().getPort();

            return Map.of(
                    "id", portNode.getId(),
                    "name", portNode.getName()
            );
        } catch (WebClientResponseException e) {
            log.error(e.getMessage(), e.getResponseBodyAsString(), e);
            switch (e.getStatusCode().value()) {
                case 400 -> throw new NeutronException(NeutronErrorCode.NEUTRON_PORT_BAD_REQUEST, e);
                case 403 -> throw new NeutronException(NeutronErrorCode.NEUTRON_PORT_FORBIDDEN, e);
                case 404 -> throw new NetworkException(NetworkErrorCode.NOT_FOUND_INTERFACE, e);
                default -> throw new NeutronException(NeutronErrorCode.NEUTRON_PORT_RETRIEVAL_FAILED, e);
            }
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

    private List<ViewInterfacesResponse> parseInterfaces(String keystoneToken, ResponseEntity<NeutronPortsResponse> response) {
        List<ViewInterfacesResponse> ports = new ArrayList<>();
        for (NeutronPortsResponse.Port portsNode : response.getBody().getPorts()) {

            boolean hasNetwork = portsNode.getNetworkId() != null && !portsNode.getNetworkId().isEmpty();
            boolean hasDevice = portsNode.getDeviceId() != null && !portsNode.getDeviceId().isEmpty();
            String serverName = hasDevice ? getServerName(keystoneToken, portsNode.getDeviceId()) : null;
            String networkName = hasNetwork ? getNetworkName(keystoneToken, portsNode.getNetworkId()) : null;

            ports.add(ViewInterfacesResponse.builder()
                    .interfaceId(portsNode.getId())
                    .interfaceName(portsNode.getName())
                    .status(InterfaceStatus.findByStatusName(portsNode.getStatus()))
                    .mac(portsNode.getMacAddress())
                    .instance(hasDevice ? ViewInterfacesResponse.Instance.builder()
                                    .instanceId(portsNode.getDeviceId())
                                    .instanceName(serverName)
                                    .build() : null)
                    .network(hasNetwork ? ViewInterfacesResponse.Network.builder()
                                    .networkId(portsNode.getNetworkId())
                                    .networkName(networkName)
                                    .subnetId(
                                            portsNode.getFixedIps() != null && !portsNode.getFixedIps().isEmpty() ?
                                                    portsNode.getFixedIps().get(0).getSubnetId() : null
                                    )
                                    .build() : null)
                    .internalIp(portsNode.getFixedIps() != null && !portsNode.getFixedIps().isEmpty() ?
                            portsNode.getFixedIps().get(0).getIpAddress() : null)
                    .externalIp(getFloatingIP(keystoneToken, portsNode.getId()))
                    .build());
        }

        return ports;
    }

    private String getServerName(String keystoneToken, String serverId) {
        try {
            ResponseEntity<com.acc.local.external.dto.nova.response.NovaServerResponse> response = serverAPIModule.showServer(keystoneToken, serverId);

            if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
                throw new NeutronException(NeutronErrorCode.NEUTRON_PORT_RETRIEVAL_FAILED);
            }

            return response.getBody().getServer().getName();
        } catch (WebClientResponseException e) {
            log.error(e.getMessage(), e.getResponseBodyAsString(), e);
            switch (e.getStatusCode().value()) {
                case 404 -> throw new NovaException(NovaErrorCode.NOVA_SERVER_NOT_FOUND);
                default -> throw new NeutronException(NeutronErrorCode.NEUTRON_PORT_RETRIEVAL_FAILED);
            }

        }
    }

    private String getNetworkName(String keystoneToken, String networkId) {
        try {
            ResponseEntity<com.acc.local.external.dto.neutron.response.NeutronNetworkResponse> response = networksAPIModule.showNeutronNetwork(keystoneToken, networkId);

            if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
                throw new NeutronException(NeutronErrorCode.NEUTRON_NETWORK_RETRIEVAL_FAILED);
            }
            return response.getBody().getNetwork().getName();
        } catch (WebClientResponseException e) {
            log.error(e.getMessage(), e.getResponseBodyAsString(), e);
            switch (e.getStatusCode().value()) {
                case 400 -> throw new NeutronException(NeutronErrorCode.NEUTRON_NETWORK_BAD_REQUEST);
                case 403 -> throw new NeutronException(NeutronErrorCode.NEUTRON_NETWORK_FORBIDDEN);
                case 404 -> throw new NetworkException(NetworkErrorCode.NOT_FOUND_NETWORK);
                default -> throw new NeutronException(NeutronErrorCode.NEUTRON_NETWORK_RETRIEVAL_FAILED);
            }

        }
    }

    private String getFloatingIP(String keystoneToken, String portId) {
        try {
            ResponseEntity<NeutronFloatingIpsResponse> response = floatingIpsAPIModule.listFloatingIps(keystoneToken,
                    Map.of("port_id", portId));

            if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
                throw new NeutronException(NeutronErrorCode.NEUTRON_FLOATING_IP_RETRIEVAL_FAILED);
            }

            if (response.getBody().getFloatingips() == null || response.getBody().getFloatingips().isEmpty()) {
                return null;
            }
            var floatingIpNode = response.getBody().getFloatingips().get(0);
            return floatingIpNode.getFloatingIpAddress();
        } catch (WebClientResponseException e) {
            log.error(e.getMessage(), e.getResponseBodyAsString(), e);
            switch (e.getStatusCode().value()) {
                case 400 -> throw new NeutronException(NeutronErrorCode.NEUTRON_FLOATING_IP_BAD_REQUEST, e);
                case 403 -> throw new NeutronException(NeutronErrorCode.NEUTRON_FLOATING_IP_FORBIDDEN, e);
                case 404 -> throw new NeutronException(NeutronErrorCode.NEUTRON_FLOATING_IP_NOT_FOUND, e);
                default -> throw new NeutronException(NeutronErrorCode.NEUTRON_FLOATING_IP_RETRIEVAL_FAILED, e);
            }
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
