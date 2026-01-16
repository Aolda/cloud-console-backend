package com.acc.local.service.modules.instance;

import com.acc.local.dto.instance.InterfaceAttachmentRequest;
import com.acc.local.dto.instance.InterfaceAttachmentResponse;
import com.acc.local.external.ports.NovaServerInterfaceExternalPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class InstanceInterfaceModule {

    private final NovaServerInterfaceExternalPort novaServerInterfaceExternalPort;

    public List<InterfaceAttachmentResponse> listInterfaces(String keystoneToken, String instanceId) {
        return novaServerInterfaceExternalPort.callListInterfaces(keystoneToken, instanceId);
    }

    public InterfaceAttachmentResponse createInterface(String keystoneToken, String instanceId, InterfaceAttachmentRequest request) {
        return novaServerInterfaceExternalPort.callCreateInterface(keystoneToken, instanceId, request);
    }

    public void detachInterface(String keystoneToken, String instanceId, String interfaceId) {
        novaServerInterfaceExternalPort.callDetachInterface(keystoneToken, instanceId, interfaceId);
    }
}

