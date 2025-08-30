package com.acc.local.controller;

import com.acc.global.properties.KeycloakProperties;
import com.acc.global.security.JwtUtils;
import com.acc.local.domain.enums.ProjectPermission;
import com.acc.local.dto.auth.CreateUserRequest;
import com.acc.local.dto.auth.CreateUserResponse;
import com.acc.local.dto.auth.UserPermissionResponse;
import com.acc.local.service.ports.AuthPort;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.keycloak.representations.idm.UserRepresentation;

import java.net.URI;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final KeycloakProperties keycloakProperties;
    private final AuthPort authPort;

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
    public String authorize(@RequestHeader("Authorization") String authorization) {
        String keycloakToken = JwtUtils.extractTokenFromHeader(authorization);
        return authPort.authenticateAndGenerateJwt(keycloakToken);
    }

    @GetMapping("/permission")
    public ResponseEntity<UserPermissionResponse> getPermission(
            @RequestParam String keystoneProjectId,
            Authentication authentication
    ) {
        // acc 토큰으로부터 userId 추출
        String userId = authentication.getName();

        // permission 추출
        ProjectPermission permission = authPort.getProjectPermission(keystoneProjectId, userId);

        UserPermissionResponse response = UserPermissionResponse.builder()
                .projectPermission(permission.name().toLowerCase())
                .projectId(keystoneProjectId)
                .build();

        return ResponseEntity.ok(response);
    }

    @PostMapping("/user")
    public ResponseEntity<CreateUserResponse> createKeystoneUser(
            @ModelAttribute @Validated CreateUserRequest request,
            Authentication authentication
    ) {
        String userId = authentication.getName();
        CreateUserResponse response = authPort.createUser(request, userId);
        
        return ResponseEntity.status(201).body(response);
    }


}
