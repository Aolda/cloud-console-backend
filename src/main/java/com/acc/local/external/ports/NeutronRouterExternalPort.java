package com.acc.local.external.ports;

import com.acc.global.common.PageResponse;
import com.acc.local.dto.network.ViewRoutersResponse;

public interface NeutronRouterExternalPort {

    PageResponse<ViewRoutersResponse> callListRouters(String keystoneToken, String projectId, String marker, String direction, int limit);
    void callDeleteRouter(String keystoneToken, String routerId);
    void callCreateRouter(String keystoneToken, String routerName, String networkId);
}
