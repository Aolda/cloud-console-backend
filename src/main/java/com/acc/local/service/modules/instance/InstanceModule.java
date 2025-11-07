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

    public PageResponse<InstanceResponse> listInstances(String token, String projectId, String marker, String direction, int limit) {
        return novaServerExternalPort.callListInstances(token, projectId, marker, direction, limit);
    }

    public void createInstance(String token, String projectId, InstanceCreateRequest request) {
        novaServerExternalPort.callCreateInstance(token, projectId, request);
    }

    public void controlInstance(String token, String projectId, String instanceId, InstanceActionRequest request) {
        novaServerActionExternalPort.callControlInstance(token, projectId, instanceId, request);
    }
}

