package com.acc.local.controller;

import com.acc.local.service.ports.AuthServicePort;
import com.acc.global.properties.KeycloakProperties;
import com.acc.global.security.jwt.JwtUtils;
import com.acc.local.dto.auth.CreateUserRequest;
import com.acc.local.dto.auth.CreateUserResponse;
import com.acc.local.dto.auth.GetUserResponse;
import com.acc.local.dto.auth.UpdateUserRequest;
import com.acc.local.dto.auth.UpdateUserResponse;
import com.acc.local.dto.auth.CreateProjectRequest;
import com.acc.local.dto.auth.CreateProjectResponse;
import com.acc.local.dto.auth.GetProjectResponse;
import com.acc.local.dto.auth.UpdateProjectRequest;
import com.acc.local.dto.auth.UpdateProjectResponse;
import com.acc.local.dto.auth.UserPermissionResponse;
import com.acc.local.dto.auth.KeystonePasswordLoginRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthController {


    private final KeycloakProperties keycloakProperties;
    private final AuthServicePort authServicePort;

    // TODO: keycloak 서버 띄워진 후 테스트 필요 (keycloak 토큰 정보의 userId로 사용자 정보 확인 가능)
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
        return authServicePort.authenticateAndGenerateJwt(keycloakToken);
    }


    @GetMapping("/permission")
    public ResponseEntity<UserPermissionResponse> getPermission(
            @RequestParam String keystoneProjectId,
            Authentication authentication
    ) {
        String userId = authentication.getName();
        UserPermissionResponse response = authServicePort.getUserPermission(keystoneProjectId, userId);

        return ResponseEntity.ok(response);
    }

    // TODO: keycloak 서버 띄워진 후 테스트 필요 (keycloak 토큰 정보의 userId로 사용자 정보 확인 가능)
    @PostMapping("/user")
    public ResponseEntity<CreateUserResponse> createKeystoneUser(
            @ModelAttribute @Validated CreateUserRequest request,
            Authentication authentication
    ) {
        String userId = authentication.getName();
        CreateUserResponse response = authServicePort.createUser(request, userId);

        return ResponseEntity.status(201).body(response);
    }

    @GetMapping("/user/{keystoneUserId}")
    public ResponseEntity<GetUserResponse> getUserDetail(
            @PathVariable String keystoneUserId,
            Authentication authentication
    ) {
        String requesterId = authentication.getName();
        GetUserResponse response = authServicePort.getUserDetail(keystoneUserId, requesterId);

        return ResponseEntity.ok(response);
    }

    @PutMapping("/user/{keystoneUserId}")
    public ResponseEntity<UpdateUserResponse> updateUser(
            @PathVariable String keystoneUserId,
            @RequestBody @Validated UpdateUserRequest request,
            Authentication authentication
    ) {
        String requesterId = authentication.getName();
        UpdateUserResponse response = authServicePort.updateUser(keystoneUserId, request, requesterId);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/user/{keystoneUserId}")
    public ResponseEntity<Void> deleteUser(
            @PathVariable String keystoneUserId,
            Authentication authentication
    ) {
        String requesterId = authentication.getName();
        authServicePort.deleteUser(keystoneUserId, requesterId);

        return ResponseEntity.noContent().build();
    }

    // TODO: keycloak 서버 띄워진 후 테스트 필요 (keycloak 토큰 정보의 userId로 사용자 정보 확인 가능)
    @PostMapping("/project")
    public ResponseEntity<CreateProjectResponse> createKeystoneProject(
            @RequestBody @Validated CreateProjectRequest request,
            Authentication authentication
    ) {
        String userId = authentication.getName();
        CreateProjectResponse response = authServicePort.createProject(request, userId);

        return ResponseEntity.status(201).body(response);
    }

    @GetMapping("/project/{keystoneProjectId}")
    public ResponseEntity<GetProjectResponse> getProjectDetail(
            @PathVariable String keystoneProjectId,
            Authentication authentication
    ) {
        String requesterId = authentication.getName();
        GetProjectResponse response = authServicePort.getProjectDetail(keystoneProjectId, requesterId);

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/project/{keystoneProjectId}")
    public ResponseEntity<UpdateProjectResponse> updateProject(
            @PathVariable String keystoneProjectId,
            @RequestBody @Validated UpdateProjectRequest request,
            Authentication authentication
    ) {
        String requesterId = authentication.getName();
        UpdateProjectResponse response = authServicePort.updateProject(keystoneProjectId, request, requesterId);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/project/{keystoneProjectId}")
    public ResponseEntity<Void> deleteProject(
            @PathVariable String keystoneProjectId,
            Authentication authentication
    ) {
        String requesterId = authentication.getName();
        authServicePort.deleteProject(keystoneProjectId, requesterId);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/token/keystone/scoped")
    public ResponseEntity<String> issueProjectScopeToken(
            @RequestParam String projectId,
            Authentication authentication
    ) {
        String userId = authentication.getName();
        String token = authServicePort.issueProjectScopeToken(projectId,userId);
        return ResponseEntity.ok(token);
    }

    // TEST 로그인
    @PostMapping("/login/general")
    public ResponseEntity<String> loginGeneral(@RequestBody @Validated KeystonePasswordLoginRequest request) {
        String jwtToken = authServicePort.authenticateKeystoneAndGenerateJwt(request);
        return ResponseEntity.ok(jwtToken);
    }

}
