package com.acc.local.external.ports;

import com.acc.global.common.PageResponse;
import com.acc.local.dto.network.ViewRoutersResponse;

import java.util.Map;

public interface NeutronRouterExternalPort {

    PageResponse<ViewRoutersResponse> callListRouters(String keystoneToken, String projectId, String marker, String direction, int limit);
    void callDeleteRouter(String keystoneToken, String routerId);
    String callCreateRouter(String keystoneToken, String routerName, String networkId);

    String callAddRouterInterface(String keystoneToken, String routerId, String subnetId);

    Map<String, String> getRouterNameAndId(String keystoneToken, String routerId);
}
