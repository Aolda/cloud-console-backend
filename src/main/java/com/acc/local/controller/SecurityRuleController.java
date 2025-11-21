package com.acc.local.controller;

import com.acc.global.security.jwt.JwtInfo;
import com.acc.local.controller.docs.SecurityRuleDocs;
import com.acc.local.dto.network.CreateSecurityRuleRequest;
import com.acc.local.service.ports.SecurityRuleServicePort;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class SecurityRuleController implements SecurityRuleDocs {

    private final SecurityRuleServicePort securityRuleServicePort;

    @Override
    public ResponseEntity<Object> createSecurityRule(Authentication authentication, String sgId, CreateSecurityRuleRequest request) {
        JwtInfo jwtInfo = (JwtInfo) authentication.getPrincipal();
        securityRuleServicePort.createSecurityRule(sgId, jwtInfo.getProjectId(), jwtInfo.getUserId(), request);
        return ResponseEntity.created(null).build();
    }

    @Override
    public ResponseEntity<Object> deleteSecurityRule(Authentication authentication, String srId) {
        JwtInfo jwtInfo = (JwtInfo) authentication.getPrincipal();
        securityRuleServicePort.deleteSecurityRule(srId, jwtInfo.getProjectId(), jwtInfo.getUserId());
        return ResponseEntity.noContent().build();
    }
}
