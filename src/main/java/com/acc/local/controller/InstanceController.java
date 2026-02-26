package com.acc.local.controller;

import com.acc.global.common.PageRequest;
import com.acc.global.common.PageResponse;
import com.acc.global.security.session.SessionPrincipal;
import com.acc.local.controller.docs.InstanceDocs;
import com.acc.local.dto.instance.InstanceActionRequest;
import com.acc.local.dto.instance.InstanceCreateRequest;
import com.acc.local.dto.instance.InstanceQuotaResponse;
import com.acc.local.dto.instance.InstanceResponse;
import com.acc.local.service.ports.InstanceServicePort;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class InstanceController implements InstanceDocs {

    private final InstanceServicePort instanceServicePort;

    @Override
    public ResponseEntity<PageResponse<InstanceResponse>> getInstances(Authentication authentication, String projectId, PageRequest page) {
        SessionPrincipal principal = (SessionPrincipal) authentication.getPrincipal();
        String sessionId = principal.getSessionId();

        PageResponse<InstanceResponse> response = instanceServicePort.getInstances(page, sessionId, projectId);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<Object> createInstance(Authentication authentication, String projectId, InstanceCreateRequest request) {
        SessionPrincipal principal = (SessionPrincipal) authentication.getPrincipal();
        String sessionId = principal.getSessionId();

        instanceServicePort.createInstance(request, sessionId, projectId);
        return ResponseEntity.created(null).build();
    }

    @Override
    public ResponseEntity<InstanceQuotaResponse> getQuota(Authentication authentication, String projectId) {
        SessionPrincipal principal = (SessionPrincipal) authentication.getPrincipal();
        String sessionId = principal.getSessionId();

        InstanceQuotaResponse response = instanceServicePort.getQuota(sessionId, projectId);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<Object> controlInstance(Authentication authentication, String projectId, String instanceId, InstanceActionRequest request) {
        SessionPrincipal principal = (SessionPrincipal) authentication.getPrincipal();
        String sessionId = principal.getSessionId();

        instanceServicePort.controlInstance(instanceId, request, sessionId, projectId);
        return ResponseEntity.ok().build();
    }
}

