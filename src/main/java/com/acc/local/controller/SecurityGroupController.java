package com.acc.local.controller;


import com.acc.global.common.PageRequest;
import com.acc.global.common.PageResponse;
import com.acc.global.security.jwt.JwtInfo;
import com.acc.local.controller.docs.SecurityGroupDocs;
import com.acc.local.dto.network.CreateSecurityGroupRequest;
import com.acc.local.service.ports.SecurityGroupServicePort;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
@RequiredArgsConstructor
public class SecurityGroupController implements SecurityGroupDocs {

    private final SecurityGroupServicePort securityGroupServicePort;

    @Override
    public ResponseEntity<Object> viewSecurityGroups(Authentication authentication, PageRequest page, String projectId) {
        JwtInfo jwtInfo = (JwtInfo) authentication.getPrincipal();
        return ResponseEntity.ok(securityGroupServicePort.listSecurityGroups(page, projectId, jwtInfo.getUserId()));

    }

    @Override
    public ResponseEntity<Object> viewSecurityGroup(Authentication authentication, String sgId, PageRequest page, String projectId) {
        JwtInfo jwtInfo = (JwtInfo) authentication.getPrincipal();
        return ResponseEntity.ok(securityGroupServicePort.getSecurityGroupDetail(page, sgId, projectId, jwtInfo.getUserId()));
    }

    @Override
    public ResponseEntity<Object> createSecurityGroup(Authentication authentication, CreateSecurityGroupRequest request, String projectId) {
        JwtInfo jwtInfo = (JwtInfo) authentication.getPrincipal();
        String id = securityGroupServicePort.createSecurityGroup(request, projectId, jwtInfo.getUserId());
        return ResponseEntity.created(null).build();
    }

    @Override
    public ResponseEntity<Object> deleteSecurityGroup(Authentication authentication, String sgId, String projectId) {
        JwtInfo jwtInfo = (JwtInfo) authentication.getPrincipal();
        securityGroupServicePort.deleteSecurityGroup(sgId, projectId, jwtInfo.getUserId());
        return ResponseEntity.noContent().build();
    }
}
