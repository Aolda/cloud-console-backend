package com.acc.local.service.ports;

import com.acc.global.common.PageRequest;
import com.acc.global.common.PageResponse;
import com.acc.local.dto.network.CreateNetworkRequest;
import com.acc.local.dto.network.ViewNetworksResponse;

public interface NetworkServicePort {


    String createNetwork(CreateNetworkRequest request, String sessionId, String projectId);

    void deleteNetwork(String networkId, String sessionId, String projectID);

    PageResponse<ViewNetworksResponse> listNetworks(PageRequest page, String sessionId, String projectId);
}
