package com.acc.local.controller;

import com.acc.global.security.session.SessionPrincipal;
import com.acc.local.controller.docs.InstanceInterfaceDocs;
import com.acc.local.dto.instance.InterfaceAttachmentRequest;
import com.acc.local.dto.instance.InterfaceAttachmentResponse;
import com.acc.local.service.ports.InstanceInterfaceServicePort;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class InstanceInterfaceController implements InstanceInterfaceDocs {

    private final InstanceInterfaceServicePort instanceInterfaceServicePort;

    @Override
    public ResponseEntity<List<InterfaceAttachmentResponse>> listInterfaces(Authentication authentication, String instanceId, String projectId) {
        SessionPrincipal principal = (SessionPrincipal) authentication.getPrincipal();
        String sessionId = principal.getSessionId();

        return ResponseEntity.ok(instanceInterfaceServicePort.listInterfaces(sessionId, projectId, instanceId));
    }

    @Override
    public ResponseEntity<InterfaceAttachmentResponse> createInterface(Authentication authentication, String instanceId, String projectId, InterfaceAttachmentRequest request) {
        SessionPrincipal principal = (SessionPrincipal) authentication.getPrincipal();
        String sessionId = principal.getSessionId();

        InterfaceAttachmentResponse response = instanceInterfaceServicePort.createInterface(sessionId, projectId, instanceId, request);
        return ResponseEntity.status(201).body(response);
    }

    @Override
    public ResponseEntity<Void> detachInterface(Authentication authentication, String instanceId, String interfaceId, String projectId) {
        SessionPrincipal principal = (SessionPrincipal) authentication.getPrincipal();
        String sessionId = principal.getSessionId();

        instanceInterfaceServicePort.detachInterface(sessionId, projectId, instanceId, interfaceId);
        return ResponseEntity.noContent().build();
    }
}

