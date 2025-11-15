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

@RestController
@RequiredArgsConstructor
public class SecurityGroupController implements SecurityGroupDocs {

    private final SecurityGroupServicePort securityGroupServicePort;

    @Override
    public ResponseEntity<Object> viewSecurityGroups(Authentication authentication, String sgId, PageRequest page) {
        JwtInfo jwtInfo = (JwtInfo) authentication.getPrincipal();
        if (sgId != null && !sgId.isEmpty()) {
            return ResponseEntity.ok(securityGroupServicePort.getSecurityGroupDetail(page, sgId, jwtInfo.getProjectId(), jwtInfo.getUserId()));
        } else {
            return ResponseEntity.ok(securityGroupServicePort.listSecurityGroups(page, jwtInfo.getProjectId(), jwtInfo.getUserId()));
        }
    }

    @Override
    public ResponseEntity<Object> createSecurityGroup(Authentication authentication, CreateSecurityGroupRequest request) {
        JwtInfo jwtInfo = (JwtInfo) authentication.getPrincipal();
        securityGroupServicePort.createSecurityGroup(request, jwtInfo.getProjectId(), jwtInfo.getUserId());
        return ResponseEntity.created(null).build();
    }

    @Override
    public ResponseEntity<Object> deleteSecurityGroup(Authentication authentication, String sgId) {
        JwtInfo jwtInfo = (JwtInfo) authentication.getPrincipal();
        securityGroupServicePort.deleteSecurityGroup(sgId, jwtInfo.getProjectId(), jwtInfo.getUserId());
        return ResponseEntity.noContent().build();
    }
}
