package com.acc.local.service.adapters.instance;

import com.acc.local.dto.instance.InterfaceAttachmentRequest;
import com.acc.local.dto.instance.InterfaceAttachmentResponse;
import com.acc.local.service.modules.auth.AuthModule;
import com.acc.local.service.modules.instance.InstanceInterfaceModule;
import com.acc.local.service.ports.InstanceInterfaceServicePort;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Primary
@RequiredArgsConstructor
public class InstanceInterfaceServiceAdapter implements InstanceInterfaceServicePort {

    private final InstanceInterfaceModule instanceInterfaceModule;
    private final AuthModule authModule;

    @Override
    public List<InterfaceAttachmentResponse> listInterfaces(String userId, String projectId, String instanceId) {
        String keystoneToken = authModule.issueProjectScopeToken(projectId, userId);
        return instanceInterfaceModule.listInterfaces(keystoneToken, instanceId);
    }

    @Override
    public InterfaceAttachmentResponse createInterface(String userId, String projectId, String instanceId, InterfaceAttachmentRequest request) {
        String keystoneToken = authModule.issueProjectScopeToken(projectId, userId);
        return instanceInterfaceModule.createInterface(keystoneToken, instanceId, request);
    }

    @Override
    public void detachInterface(String userId, String projectId, String instanceId, String interfaceId) {
        String keystoneToken = authModule.issueProjectScopeToken(projectId, userId);
        instanceInterfaceModule.detachInterface(keystoneToken, instanceId, interfaceId);
    }
}

