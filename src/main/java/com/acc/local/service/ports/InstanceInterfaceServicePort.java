package com.acc.local.service.ports;

import com.acc.local.dto.instance.InterfaceAttachmentRequest;
import com.acc.local.dto.instance.InterfaceAttachmentResponse;

import java.util.List;

public interface InstanceInterfaceServicePort {

    InterfaceAttachmentResponse createInterface(String userId, String projectId, String instanceId, InterfaceAttachmentRequest request);
    List<InterfaceAttachmentResponse> listInterfaces(String userId, String projectId, String instanceId);
    void detachInterface(String userId, String projectId, String instanceId, String interfaceId);
}

