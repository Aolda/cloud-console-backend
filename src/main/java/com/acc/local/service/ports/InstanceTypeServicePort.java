package com.acc.local.service.ports;

import com.acc.global.common.PageRequest;
import com.acc.global.common.PageResponse;
import com.acc.local.dto.type.InstanceTypeCreateRequest;
import com.acc.local.dto.type.InstanceTypeResponse;

public interface InstanceTypeServicePort {

    PageResponse<InstanceTypeResponse> listUserInstanceTypes(String sessionId, String projectId, String architect, PageRequest page);
    PageResponse<InstanceTypeResponse> listAdminInstanceTypes(String sessionId, String architect, PageRequest page);
    void createInstanceType(String sessionId, InstanceTypeCreateRequest request);
}
