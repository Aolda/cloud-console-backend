package com.acc.local.service.modules.type;

import com.acc.global.common.PageResponse;
import com.acc.local.dto.type.InstanceTypeCreateRequest;
import com.acc.local.dto.type.InstanceTypeResponse;
import com.acc.local.external.ports.NovaFlavorExternalPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class InstanceTypeModule {

    private final NovaFlavorExternalPort novaFlavorExternalPort;

    public PageResponse<InstanceTypeResponse> listInstanceTypes(String keystoneToken, String marker, String direction, int limit) {
        return novaFlavorExternalPort.callListFlavors(keystoneToken, null, marker, direction, limit);
    }

    public PageResponse<InstanceTypeResponse> listInstanceTypesByArchitect(String keystoneToken, String architect, String marker, String direction, int limit) {
        return novaFlavorExternalPort.callListFlavors(keystoneToken, architect, marker, direction, limit);
    }

    public void createInstanceType(String keystoneToken, InstanceTypeCreateRequest request) {
        novaFlavorExternalPort.callCreateFlavor(keystoneToken, request);
    }
}
