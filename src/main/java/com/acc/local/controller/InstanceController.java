package com.acc.local.controller;

import com.acc.global.common.PageRequest;
import com.acc.global.common.PageResponse;
import com.acc.global.security.jwt.JwtInfo;
import com.acc.local.controller.docs.InstanceDocs;
import com.acc.local.dto.instance.InstanceActionRequest;
import com.acc.local.dto.instance.InstanceCreateRequest;
import com.acc.local.dto.instance.InstanceResponse;
import com.acc.local.service.ports.InstanceServicePort;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class InstanceController implements InstanceDocs {

    private final InstanceServicePort instanceServicePort;

    @Override
    public ResponseEntity<PageResponse<InstanceResponse>> getInstances(Authentication authentication, PageRequest page) {
        JwtInfo jwtInfo = (JwtInfo) authentication.getPrincipal();
        String userId = jwtInfo.getUserId();
        String projectId = jwtInfo.getProjectId();

        PageResponse<InstanceResponse> response = instanceServicePort.getInstances(page, userId, projectId);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<Object> createInstance(Authentication authentication, InstanceCreateRequest request) {
        JwtInfo jwtInfo = (JwtInfo) authentication.getPrincipal();
        String userId = jwtInfo.getUserId();
        String projectId = jwtInfo.getProjectId();

        instanceServicePort.createInstance(request, userId, projectId);
        return ResponseEntity.created(null).build();
    }

    @Override
    public ResponseEntity<Object> controlInstance(Authentication authentication, String instanceId, InstanceActionRequest request) {
        JwtInfo jwtInfo = (JwtInfo) authentication.getPrincipal();
        String userId = jwtInfo.getUserId();
        String projectId = jwtInfo.getProjectId();

        instanceServicePort.controlInstance(instanceId, request, userId, projectId);
        return ResponseEntity.ok().build();
    }
}

