package com.acc.local.service.ports;

import com.acc.global.common.PageRequest;
import com.acc.global.common.PageResponse;
import com.acc.local.dto.network.CreateRouterRequest;
import com.acc.local.dto.network.ViewRoutersResponse;

public interface RouterServicePort {
    PageResponse<ViewRoutersResponse> listRouters(PageRequest page, String token);
    void deleteRouter(String routerId, String token);
    void createRouter(CreateRouterRequest request, String token);
}
