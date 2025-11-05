package com.acc.local.service.modules.network;

import com.acc.global.common.PageResponse;
import com.acc.global.exception.network.NetworkErrorCode;
import com.acc.global.exception.network.NetworkException;
import com.acc.local.dto.network.CreateNetworkRequest;
import com.acc.local.dto.network.ViewNetworksResponse;
import com.acc.local.dto.network.ViewRoutersResponse;
import com.acc.local.external.ports.NeutronNetworkExternalPort;
import com.acc.local.external.ports.NeutronRouterExternalPort;
import com.acc.local.external.ports.NeutronSubnetExternalPort;
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
}
