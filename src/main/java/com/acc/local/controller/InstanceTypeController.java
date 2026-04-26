package com.acc.local.controller;

import com.acc.global.common.PageRequest;
import com.acc.global.common.PageResponse;
import com.acc.global.security.session.SessionPrincipal;
import com.acc.local.controller.docs.InstanceTypeDocs;
import com.acc.local.dto.type.InstanceTypeCreateRequest;
import com.acc.local.dto.type.InstanceTypeResponse;
import com.acc.local.service.ports.InstanceTypeServicePort;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class InstanceTypeController implements InstanceTypeDocs {

    private final InstanceTypeServicePort instanceTypeServicePort;

    @Override
    public ResponseEntity<PageResponse<InstanceTypeResponse>> getUserInstanceTypes(Authentication authentication, PageRequest page, String architect, String projectId) {
        SessionPrincipal principal = (SessionPrincipal) authentication.getPrincipal();
        return ResponseEntity.ok(instanceTypeServicePort.listUserInstanceTypes(principal.getSessionId(), projectId, architect, page));
    }

    @Override
    public ResponseEntity<PageResponse<InstanceTypeResponse>> getAdminInstanceTypes(Authentication authentication, PageRequest page, String architect) {
        SessionPrincipal principal = (SessionPrincipal) authentication.getPrincipal();
        return ResponseEntity.ok(instanceTypeServicePort.listAdminInstanceTypes(principal.getSessionId(), architect, page));
    }

    @Override
    public ResponseEntity<Object> createInstanceType(Authentication authentication, InstanceTypeCreateRequest request) {
        SessionPrincipal principal = (SessionPrincipal) authentication.getPrincipal();
        instanceTypeServicePort.createInstanceType(principal.getSessionId(), request);
        return ResponseEntity.created(null).build();
    }
}
