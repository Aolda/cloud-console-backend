package com.acc.local.external.ports;

import com.acc.global.common.PageResponse;
import com.acc.local.dto.type.InstanceTypeCreateRequest;
import com.acc.local.dto.type.InstanceTypeResponse;

public interface NovaFlavorExternalPort {

    PageResponse<InstanceTypeResponse> callListFlavors(String keystoneToken, String architect, String marker, String direction, int limit);
    void callCreateFlavor(String keystoneToken, InstanceTypeCreateRequest request);
}
