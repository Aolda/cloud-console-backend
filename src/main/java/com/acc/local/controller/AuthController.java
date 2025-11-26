package com.acc.local.controller;

import com.acc.global.security.jwt.JwtInfo;
import com.acc.local.dto.project.UserPermissionResponse;
import com.acc.local.controller.docs.AuthDocs;
import com.acc.local.service.ports.AuthServicePort;
import com.acc.global.properties.KeycloakProperties;
import com.acc.global.properties.OAuth2Properties;
import com.acc.global.security.jwt.JwtUtils;
import com.acc.local.dto.auth.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class AuthController implements AuthDocs {


    private final KeycloakProperties keycloakProperties;
    private final AuthServicePort authServicePort;
    private final OAuth2Properties oAuth2Properties;

    // TODO: keycloak 서버 띄워진 후 테스트 필요 (keycloak 토큰 정보의 userId로 사용자 정보 확인 가능)
    @Deprecated
    @GetMapping("/login")
    public ResponseEntity<Void> login() {
        String keycloakLoginUrl = keycloakProperties.getLoginUrl();
        return ResponseEntity.status(302)
                .location(URI.create(keycloakLoginUrl))
                .build();
    }


    @Deprecated
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
    @Deprecated
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

    @Deprecated
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

    @Deprecated
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

    @Deprecated
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

    @Deprecated
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

    @Override
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

        // SameSite 속성 설정 (크로스 사이트 쿠키 허용)
        refreshTokenCookie.setAttribute("SameSite", "None");

        // 도메인 설정 (설정되어 있을 때만)
        String domain = oAuth2Properties.getCookie().getDomain();
        if (domain != null && !domain.isBlank()) {
            refreshTokenCookie.setDomain(domain);
            log.info("[쿠키 설정] acc-refresh-token 쿠키 도메인: {}, SameSite: None", domain);
        } else {
            log.info("[쿠키 설정] acc-refresh-token 쿠키 도메인: 미설정 (현재 호스트 사용), SameSite: None");
        }
        response.addCookie(refreshTokenCookie);

        // 3. Access Token은 Response Body에 반환
        LoginResponse loginResponse = new LoginResponse(tokens.accessToken());
        return ResponseEntity.ok(loginResponse);
    }


    @Override
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
            @CookieValue("acc-refresh-token") String refreshToken
    ) {
        LoginResponse loginResponse = authServicePort.refreshToken(refreshToken);
        return ResponseEntity.ok(loginResponse);
    }

    @PostMapping("/signup")
    public ResponseEntity<SignupResponse> signup(
            @RequestBody @Validated SignupRequest request,
            @CookieValue("oauth-verification-token") String verificationToken
    ) {

        SignupResponse response = authServicePort.signup(request, verificationToken);

        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<LoginedUserProfileResponse> getLoginUserInformation(Authentication authentication) {
        JwtInfo jwtInfo = (JwtInfo) authentication.getPrincipal();
        String userId = jwtInfo.getUserId();

        LoginedUserProfileResponse loginedUserProfileResponse = authServicePort.getUserLoginedProfile(userId);
        return ResponseEntity.ok(loginedUserProfileResponse);
    }

}
