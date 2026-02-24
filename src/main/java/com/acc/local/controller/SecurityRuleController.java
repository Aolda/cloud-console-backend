package com.acc.local.controller;

import com.acc.global.security.session.SessionPrincipal;
import com.acc.local.controller.docs.SecurityRuleDocs;
import com.acc.local.dto.network.CreateSecurityRuleRequest;
import com.acc.local.service.ports.SecurityRuleServicePort;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
@RequiredArgsConstructor
public class SecurityRuleController implements SecurityRuleDocs {

    private final SecurityRuleServicePort securityRuleServicePort;

    @Override
    public ResponseEntity<Object> createSecurityRule(Authentication authentication, CreateSecurityRuleRequest request, String projectId) {
        SessionPrincipal principal = (SessionPrincipal) authentication.getPrincipal();
        String id = securityRuleServicePort.createSecurityRule(projectId, principal.getSessionId(), request);
        return ResponseEntity.created(null).build();
    }

    @Override
    public ResponseEntity<Object> deleteSecurityRule(Authentication authentication, String srId, String projectId) {
        SessionPrincipal principal = (SessionPrincipal) authentication.getPrincipal();
        securityRuleServicePort.deleteSecurityRule(srId, projectId, principal.getSessionId());
        return ResponseEntity.noContent().build();
    }
}
