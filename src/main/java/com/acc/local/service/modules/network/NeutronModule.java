package com.acc.local.service.modules.network;

import com.acc.global.common.PageResponse;
import com.acc.local.dto.network.*;
import com.acc.local.external.ports.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class NeutronModule {

    private final NeutronNetworkExternalPort neutronNetworkExternalPort;
    private final NeutronSubnetExternalPort neutronSubnetExternalPort;
    private final NeutronRouterExternalPort neutronRouterExternalPort;
    private final NeutronPortExternalPort neutronPortExternalPort;
    private final NeutronFloatingIpExternalPort neutronFloatingIpExternalPort;

    public String createGeneralNetwork(CreateNetworkRequest request, String keystoneToken) {
        return neutronNetworkExternalPort.callCreateGeneralNetwork(keystoneToken,
                request.getNetworkName(),
                request.getDescription(),
                request.getMtu());
    }

    public boolean canDeleteNetwork(String keystoneToken, String networkId) {
        Map<String, String> network = neutronNetworkExternalPort.getNetworkNameAndId(keystoneToken, networkId);
        return !network.get("name").equals("default-network");
    }

    public void deleteNetwork(String keystoneToken, String networkId) {
        neutronNetworkExternalPort.callDeleteNetwork(keystoneToken, networkId);
    }

    public PageResponse<ViewNetworksResponse> listNetworks(String keystoneToken, String projectId, String marker, String direction, int limit) {
        return neutronNetworkExternalPort.callListNetworks(keystoneToken, projectId, marker, direction, limit);
    }

    public String getProviderNetworkId(String keystoneToken) {
        return neutronNetworkExternalPort.getProviderNetwork(keystoneToken).get("id");
    }

    public void createSubnet(String keystoneToken, List<CreateNetworkRequest.Subnet> subnets, String networkId) {
        neutronSubnetExternalPort.callCreateSubnet(keystoneToken, subnets, networkId);
    }

    /* --- Routers --- */
    public PageResponse<ViewRoutersResponse> listRouters(String keystoneToken, String projectId, String marker, String direction, int limit) {
        return neutronRouterExternalPort.callListRouters(keystoneToken, projectId, marker, direction, limit);
    }

    public void deleteRouter(String keystoneToken, String routerId) {
        neutronRouterExternalPort.callDeleteRouter(keystoneToken, routerId);
    }

    public void createRouter(String keystoneToken, String routerName, boolean isExternal) {
        String networkId = null;
        if (isExternal) {
            networkId = getProviderNetworkId(keystoneToken);
        }
        neutronRouterExternalPort.callCreateRouter(keystoneToken, routerName, networkId);
    }

    /* --- External IPs --- */
    public boolean allocateExternalIpToInterface(String keystoneToken, String floatingNetworkId, String portId) {
        try {
            neutronFloatingIpExternalPort.allocateFloatingIpToPort(keystoneToken, floatingNetworkId, portId);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    public void releaseExternalIpFromInterface(String keystoneToken, String ExternalIpId) {
        neutronFloatingIpExternalPort.releaseFloatingIpFromPort(keystoneToken, ExternalIpId);
    }

    /* --- Interfaces --- */
    public String createInterface(String keystoneToken, CreateInterfaceRequest request) {
        return neutronPortExternalPort.callCreatePort(keystoneToken,
                request.getNetworkId(),
                request.getInterfaceName(),
                request.getSubnetId(),
                request.getSecurityGroupIds(),
                request.getDescription()).get("id");
    }

    public Map<String, String> getExternalIpByInterfaceId(String keystoneToken, String interfaceId) {
        return neutronFloatingIpExternalPort.getFloatingIpInfo(keystoneToken, interfaceId);
    }

    public void deleteInterface(String keystoneToken, String portId) {
        neutronPortExternalPort.callDeletePort(keystoneToken, portId);
    }

    public PageResponse<ViewInterfacesResponse> listInterfaces(String keystoneToken, String projectId, String marker, String direction, int limit, String instanceId, String networkId) {
        return neutronPortExternalPort.callListPorts(keystoneToken, projectId, marker, direction, limit, instanceId, networkId);
    }
}
