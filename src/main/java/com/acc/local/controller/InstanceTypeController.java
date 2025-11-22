package com.acc.local.controller;

import com.acc.global.common.PageRequest;
import com.acc.global.common.PageResponse;
import com.acc.global.security.jwt.JwtInfo;
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
    public ResponseEntity<PageResponse<InstanceTypeResponse>> getInstanceTypes(Authentication authentication, PageRequest page) {
        JwtInfo jwtInfo = (JwtInfo) authentication.getPrincipal();
        return ResponseEntity.ok(instanceTypeServicePort.listInstanceTypes(jwtInfo.getUserId(), jwtInfo.getProjectId(), page));
    }

    @Override
    public ResponseEntity<PageResponse<InstanceTypeResponse>> getAdminInstanceTypes(Authentication authentication, PageRequest page, String architect) {
        JwtInfo jwtInfo = (JwtInfo) authentication.getPrincipal();
        return ResponseEntity.ok(instanceTypeServicePort.listAdminInstanceTypes(jwtInfo.getUserId(), jwtInfo.getProjectId(), architect, page));
    }

    @Override
    public ResponseEntity<Object> createInstanceType(Authentication authentication, InstanceTypeCreateRequest request) {
        JwtInfo jwtInfo = (JwtInfo) authentication.getPrincipal();
        instanceTypeServicePort.createInstanceType(jwtInfo.getUserId(), jwtInfo.getProjectId(), request);
        return ResponseEntity.created(null).build();
    }
}
