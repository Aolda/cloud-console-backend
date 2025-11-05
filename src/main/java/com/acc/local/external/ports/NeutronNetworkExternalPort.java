package com.acc.local.external.ports;

import com.acc.global.common.PageResponse;
import com.acc.local.dto.network.ViewNetworksResponse;

import java.util.List;
import java.util.Map;

public interface NeutronNetworkExternalPort {
    String callCreateGeneralNetwork(String keystoneToken, String name, String description, int mtu);
    PageResponse<ViewNetworksResponse> callListNetworks(String keystoneToken, String projectId, String marker, String direction, int limit);
    void callDeleteNetwork(String keystoneToken, String networkId);
    Map<String, String> getNetworkNameAndId(String keystoneToken, String networkId);
}
