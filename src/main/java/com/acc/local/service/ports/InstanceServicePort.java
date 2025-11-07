package com.acc.local.service.ports;

import com.acc.global.common.PageRequest;
import com.acc.global.common.PageResponse;
import com.acc.local.dto.instance.InstanceActionRequest;
import com.acc.local.dto.instance.InstanceCreateRequest;
import com.acc.local.dto.instance.InstanceResponse;

public interface InstanceServicePort {

    PageResponse<InstanceResponse> getInstances(PageRequest page, String token);
    void createInstance(InstanceCreateRequest request, String token);
    void controlInstance(String instanceId, InstanceActionRequest request, String token);
}
