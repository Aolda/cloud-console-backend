package com.acc.local.controller;

import com.acc.global.properties.KeycloakProperties;
import com.acc.local.domain.enums.ProjectPermission;
import com.acc.local.dto.auth.UserPermissionResponse;
import com.acc.local.service.ports.AuthPort;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.net.URI;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthPort authPort;
    private final KeycloakProperties keycloakProperties;

    @GetMapping("/token")
    public String issueToken() {
        return authPort.issueKeystoneToken();
    }

    @GetMapping("/login")
    public ResponseEntity<Void> login() {
        String keycloakLoginUrl = keycloakProperties.getLoginUrl();
        return ResponseEntity.status(302)
                .location(URI.create(keycloakLoginUrl))
                .build();
    }

    @GetMapping("/login/authorize")
    public String authorize(@RequestParam String userId, @RequestParam String keycloakToken) {
        return authPort.authenticateAndGenerateJwt(userId, keycloakToken).block();
    }

    @GetMapping("/permission")
    public ResponseEntity<UserPermissionResponse> getPermission(
            @RequestParam String keystoneProjectId,
            Authentication authentication
            ) {
        // acc 토큰으로부터 userIdx 추출
        Long userIdx = Long.parseLong(authentication.getName());

        // permission 추출
        String permission = authPort.getProjectPermission(keystoneProjectId,userIdx).block().name();

        UserPermissionResponse response = UserPermissionResponse.builder()
                .projectPermission(permission)
                .projectId(keystoneProjectId)
                .build();

        return ResponseEntity.ok(response);
    }
}
