package com.acc.local.service.ports;

import com.acc.global.common.PageRequest;
import com.acc.global.common.PageResponse;
import com.acc.local.dto.network.CreateNetworkRequest;
import com.acc.local.dto.network.ViewNetworksResponse;
import org.springframework.data.domain.Pageable;

public interface NetworkServicePort {


    void createNetwork(CreateNetworkRequest request, String userId, String projectId);

    void deleteNetwork(String networkId, String userId, String projectID);

    PageResponse<ViewNetworksResponse> listNetworks(PageRequest page, String userId, String projectId);
}
