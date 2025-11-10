package com.acc.local.controller;

import com.acc.global.security.jwt.JwtInfo;
import com.acc.local.service.ports.AuthServicePort;
import com.acc.global.properties.KeycloakProperties;
import com.acc.global.security.jwt.JwtUtils;
import com.acc.local.dto.auth.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
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
        JwtInfo jwtInfo = (JwtInfo) authentication.getPrincipal();
        String userId = jwtInfo.getUserId();
        UserPermissionResponse response = authServicePort.getUserPermission(keystoneProjectId, userId);

        return ResponseEntity.ok(response);
    }

    // TODO: keycloak 서버 띄워진 후 테스트 필요 (keycloak 토큰 정보의 userId로 사용자 정보 확인 가능)
    @PostMapping("/user")
    public ResponseEntity<CreateUserResponse> createKeystoneUser(
            @ModelAttribute @Validated CreateUserRequest request,
            Authentication authentication
    ) {
        JwtInfo jwtInfo = (JwtInfo) authentication.getPrincipal();
        String userId = jwtInfo.getUserId();
        CreateUserResponse response = authServicePort.createUser(request, userId);

        return ResponseEntity.status(201).body(response);
    }

    @GetMapping("/user/{keystoneUserId}")
    public ResponseEntity<GetUserResponse> getUserDetail(
            @PathVariable String keystoneUserId,
            Authentication authentication
    ) {
        JwtInfo jwtInfo = (JwtInfo) authentication.getPrincipal();
        String requesterId = jwtInfo.getUserId();
        GetUserResponse response = authServicePort.getUserDetail(keystoneUserId, requesterId);

        return ResponseEntity.ok(response);
    }

    @PutMapping("/user/{keystoneUserId}")
    public ResponseEntity<UpdateUserResponse> updateUser(
            @PathVariable String keystoneUserId,
            @RequestBody @Validated UpdateUserRequest request,
            Authentication authentication
    ) {
        JwtInfo jwtInfo = (JwtInfo) authentication.getPrincipal();
        String requesterId = jwtInfo.getUserId();
        UpdateUserResponse response = authServicePort.updateUser(keystoneUserId, request, requesterId);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/user/{keystoneUserId}")
    public ResponseEntity<Void> deleteUser(
            @PathVariable String keystoneUserId,
            Authentication authentication
    ) {
        JwtInfo jwtInfo = (JwtInfo) authentication.getPrincipal();
        String requesterId = jwtInfo.getUserId();
        authServicePort.deleteUser(keystoneUserId, requesterId);

        return ResponseEntity.noContent().build();
    }

    // TODO: keycloak 서버 띄워진 후 테스트 필요 (keycloak 토큰 정보의 userId로 사용자 정보 확인 가능)
    @PostMapping("/project")
    public ResponseEntity<CreateProjectResponse> createKeystoneProject(
            @RequestBody @Validated CreateProjectRequest request,
            Authentication authentication
    ) {
        JwtInfo jwtInfo = (JwtInfo) authentication.getPrincipal();
        String userId = jwtInfo.getUserId();
        CreateProjectResponse response = authServicePort.createProject(request, userId);

        return ResponseEntity.status(201).body(response);
    }

    @GetMapping("/project/{keystoneProjectId}")
    public ResponseEntity<GetProjectResponse> getProjectDetail(
            @PathVariable String keystoneProjectId,
            Authentication authentication
    ) {
        JwtInfo jwtInfo = (JwtInfo) authentication.getPrincipal();
        String requesterId = jwtInfo.getUserId();
        GetProjectResponse response = authServicePort.getProjectDetail(keystoneProjectId, requesterId);

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/project/{keystoneProjectId}")
    public ResponseEntity<UpdateProjectResponse> updateProject(
            @PathVariable String keystoneProjectId,
            @RequestBody @Validated UpdateProjectRequest request,
            Authentication authentication
    ) {
        JwtInfo jwtInfo = (JwtInfo) authentication.getPrincipal();
        String requesterId = jwtInfo.getUserId();
        UpdateProjectResponse response = authServicePort.updateProject(keystoneProjectId, request, requesterId);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/project/{keystoneProjectId}")
    public ResponseEntity<Void> deleteProject(
            @PathVariable String keystoneProjectId,
            Authentication authentication
    ) {
        JwtInfo jwtInfo = (JwtInfo) authentication.getPrincipal();
        String requesterId = jwtInfo.getUserId();
        authServicePort.deleteProject(keystoneProjectId, requesterId);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/token/keystone/scoped")
    public ResponseEntity<String> issueProjectScopeToken(
            @RequestParam String projectId,
            Authentication authentication
    ) {
        JwtInfo jwtInfo = (JwtInfo) authentication.getPrincipal();
        String userId = jwtInfo.getUserId();
        String token = authServicePort.issueProjectScopeToken(projectId, userId);
        return ResponseEntity.ok(token);
    }

    // TEST 로그인
    @PostMapping("/login/general")
    public ResponseEntity<String> loginGeneral(@RequestBody @Validated KeystonePasswordLoginRequest request) {
        String jwtToken = authServicePort.authenticateKeystoneAndGenerateJwt(request);
        return ResponseEntity.ok(jwtToken);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @RequestBody @Validated KeystonePasswordLoginRequest request,
            HttpServletResponse response
    ) {
        // 1. Service에서 LoginTokens DTO 받기
        LoginTokens tokens = authServicePort.login(request);

        // 2. Refresh Token을 Cookie에 설정
        Cookie refreshTokenCookie = new Cookie("acc-refresh-token", tokens.refreshToken());
        refreshTokenCookie.setHttpOnly(true);
        refreshTokenCookie.setSecure(true);
        refreshTokenCookie.setPath("/");
        refreshTokenCookie.setMaxAge(7 * 24 * 60 * 60); // 7일
        response.addCookie(refreshTokenCookie);

        // 3. Access Token은 Response Body에 반환
        LoginResponse loginResponse = new LoginResponse(tokens.accessToken());
        return ResponseEntity.ok(loginResponse);
    }

    @PostMapping("/tokens/project")
    public ResponseEntity<ProjectTokenResponse> issueProjectToken(
            @RequestBody @Validated ProjectTokenRequest request,
            Authentication authentication
    ) {
        JwtInfo jwtInfo = (JwtInfo) authentication.getPrincipal();
        String userId = jwtInfo.getUserId();
        ProjectTokenResponse response = authServicePort.issueProjectAccessToken(userId, request.projectId());

        return ResponseEntity.ok(response);
    }

    @PostMapping("/login/refresh")
    public ResponseEntity<LoginResponse> refreshToken(
            @RequestBody @Validated RefreshTokenRequest request
    ) {
        LoginResponse loginResponse = authServicePort.refreshToken(request.refreshToken());
        return ResponseEntity.ok(loginResponse);
    }

    @PostMapping("/signup")
    public ResponseEntity<SignupResponse> signup(
            @RequestBody @Validated SignupRequest request
    ) {
        SignupResponse response = authServicePort.signup(request);

        return ResponseEntity.ok(response);
    }




}
