package com.acc.local.external.ports;

import com.acc.global.common.PageResponse;
import com.acc.local.dto.instance.InstanceCreateRequest;
import com.acc.local.dto.instance.InstanceResponse;

public interface NovaServerExternalPort {

    PageResponse<InstanceResponse> callListInstances(String token, String projectId, String marker, String direction, int limit);
    void callCreateInstance(String token, String projectId, InstanceCreateRequest request);
}
