package com.acc.local.controller;

import com.acc.global.security.jwt.JwtInfo;
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
        JwtInfo jwtInfo = (JwtInfo) authentication.getPrincipal();
        String userId = jwtInfo.getUserId();

        return ResponseEntity.ok(instanceInterfaceServicePort.listInterfaces(userId, projectId, instanceId));
    }

    @Override
    public ResponseEntity<InterfaceAttachmentResponse> createInterface(Authentication authentication, String instanceId, String projectId, InterfaceAttachmentRequest request) {
        JwtInfo jwtInfo = (JwtInfo) authentication.getPrincipal();
        String userId = jwtInfo.getUserId();

        InterfaceAttachmentResponse response = instanceInterfaceServicePort.createInterface(userId, projectId, instanceId, request);
        return ResponseEntity.status(201).body(response);
    }

    @Override
    public ResponseEntity<Void> detachInterface(Authentication authentication, String instanceId, String interfaceId, String projectId) {
        JwtInfo jwtInfo = (JwtInfo) authentication.getPrincipal();
        String userId = jwtInfo.getUserId();

        instanceInterfaceServicePort.detachInterface(userId, projectId, instanceId, interfaceId);
        return ResponseEntity.noContent().build();
    }
}

