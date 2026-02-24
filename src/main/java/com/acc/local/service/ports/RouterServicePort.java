package com.acc.local.service.ports;

import com.acc.global.common.PageRequest;
import com.acc.global.common.PageResponse;
import com.acc.local.dto.network.CreateRouterRequest;
import com.acc.local.dto.network.ViewRoutersResponse;

public interface RouterServicePort {
    String createRouter(CreateRouterRequest request, String sessionId, String projectId);

    void deleteRouter(String routerId, String sessionId, String projectId);

    PageResponse<ViewRoutersResponse> listRouters(PageRequest page, String sessionId, String projectId);

    void connectRouterToSubnet(String routerId, String subnetId, String sessionId, String projectId);

    void disconnectRouterFromSubnet(String routerId, String subnetId, String sessionId, String projectId);
}
