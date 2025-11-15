package com.acc.local.service.modules.instance;

import com.acc.global.common.PageResponse;
import com.acc.local.dto.instance.InstanceActionRequest;
import com.acc.local.dto.instance.InstanceCreateRequest;
import com.acc.local.dto.instance.InstanceResponse;
import com.acc.local.external.ports.NovaServerActionExternalPort;
import com.acc.local.external.ports.NovaServerExternalPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class InstanceModule {

    private final NovaServerExternalPort novaServerExternalPort;
    private final NovaServerActionExternalPort novaServerActionExternalPort;

    public PageResponse<InstanceResponse> listInstances(String keystoneToken, String projectId, String marker, String direction, int limit) {
        return novaServerExternalPort.callListInstances(keystoneToken, projectId, marker, direction, limit);
    }

    public void createInstance(String keystoneToken, String projectId, InstanceCreateRequest request) {
        novaServerExternalPort.callCreateInstance(keystoneToken, projectId, request);
    }

    public void controlInstance(String keystoneToken, String projectId, String instanceId, InstanceActionRequest request) {
        novaServerActionExternalPort.callControlInstance(keystoneToken, projectId, instanceId, request);
    }
}

