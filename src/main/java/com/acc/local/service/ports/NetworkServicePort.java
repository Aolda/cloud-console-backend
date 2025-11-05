package com.acc.local.service.ports;

import com.acc.global.common.PageRequest;
import com.acc.global.common.PageResponse;
import com.acc.local.dto.network.CreateNetworkRequest;
import com.acc.local.dto.network.ViewNetworksResponse;
import org.springframework.data.domain.Pageable;

public interface NetworkServicePort {

    void createNetwork(CreateNetworkRequest request, String token);
    void deleteNetwork(String networkId, String token);
    PageResponse<ViewNetworksResponse> listNetworks(PageRequest page, String token);

}
