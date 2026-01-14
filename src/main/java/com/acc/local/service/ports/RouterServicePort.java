package com.acc.local.service.ports;

import com.acc.global.common.PageRequest;
import com.acc.global.common.PageResponse;
import com.acc.local.dto.network.CreateRouterRequest;
import com.acc.local.dto.network.ViewRoutersResponse;

public interface RouterServicePort {
    String createRouter(CreateRouterRequest request, String userId, String projectId);

    void deleteRouter(String routerId, String userId, String projectId);

    PageResponse<ViewRoutersResponse> listRouters(PageRequest page, String userId, String projectId);

    void connectRouterToSubnet(String routerId, String subnetId, String userId, String projectId);

    void disconnectRouterFromSubnet(String routerId, String subnetId, String userId, String projectId);
}
