package com.acc.local.service.adapters.instance;

import com.acc.local.dto.instance.InterfaceAttachmentRequest;
import com.acc.local.dto.instance.InterfaceAttachmentResponse;
import com.acc.local.service.modules.instance.InstanceInterfaceModule;
import com.acc.local.service.modules.session.SessionModule;
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
    private final SessionModule sessionModule;

    @Override
    public List<InterfaceAttachmentResponse> listInterfaces(String sessionId, String projectId, String instanceId) {
        String keystoneToken = sessionModule.getKeystoneScopedToken(sessionId, projectId);
        return instanceInterfaceModule.listInterfaces(keystoneToken, instanceId);
    }

    @Override
    public InterfaceAttachmentResponse createInterface(String sessionId, String projectId, String instanceId, InterfaceAttachmentRequest request) {
        String keystoneToken = sessionModule.getKeystoneScopedToken(sessionId, projectId);
        return instanceInterfaceModule.createInterface(keystoneToken, instanceId, request);
    }

    @Override
    public void detachInterface(String sessionId, String projectId, String instanceId, String interfaceId) {
        String keystoneToken = sessionModule.getKeystoneScopedToken(sessionId, projectId);
        instanceInterfaceModule.detachInterface(keystoneToken, instanceId, interfaceId);
    }
}
