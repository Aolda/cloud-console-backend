package com.acc.local.controller;

import com.acc.global.properties.JwtProperties;
import com.acc.global.security.session.SessionPrincipal;
import com.acc.local.dto.project.UserPermissionResponse;
import com.acc.local.controller.docs.AuthDocs;
import com.acc.local.service.ports.AuthServicePort;
import com.acc.global.properties.OAuth2Properties;
import com.acc.global.security.jwt.JwtUtils;
import com.acc.local.dto.auth.*;
import com.acc.global.exception.auth.AuthErrorCode;
import com.acc.global.exception.auth.AuthServiceException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
@Slf4j
public class AuthController implements AuthDocs {


    private final AuthServicePort authServicePort;
    private final OAuth2Properties oAuth2Properties;
    private final JwtUtils jwtUtils;



    @GetMapping("/permission")
    public ResponseEntity<UserPermissionResponse> getPermission(
            @RequestParam String keystoneProjectId,
            Authentication authentication
    ) {
        SessionPrincipal principal = (SessionPrincipal) authentication.getPrincipal();
        String userId = principal.getKeystoneUserId();
        UserPermissionResponse response = authServicePort.getUserPermission(keystoneProjectId, userId);

        return ResponseEntity.ok(response);
    }

    @Deprecated
    @GetMapping("/user/{keystoneUserId}")
    public ResponseEntity<GetUserResponse> getUserDetail(
            @PathVariable String keystoneUserId,
            Authentication authentication
    ) {
        SessionPrincipal principal = (SessionPrincipal) authentication.getPrincipal();
        String requesterId = principal.getKeystoneUserId();
        GetUserResponse response = authServicePort.getUserDetail(keystoneUserId, requesterId);

        return ResponseEntity.ok(response);
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
        refreshTokenCookie.setMaxAge(jwtUtils.getRefreshTokenExpirationSeconds()); // 7일

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

    @PostMapping("/login/refresh")
    public ResponseEntity<LoginResponse> refreshToken(
            @CookieValue(value = "acc-refresh-token", required = false) String refreshToken,
            HttpServletResponse response
    ) {
        if (refreshToken == null) {
            throw new AuthServiceException(AuthErrorCode.MISSING_REFRESH_TOKEN);
        }

        // 1. Service에서 LoginTokens DTO 받기
        LoginTokens tokens = authServicePort.refreshToken(refreshToken);

        // 2. 새로운 Refresh Token을 Cookie에 설정
        Cookie refreshTokenCookie = new Cookie("acc-refresh-token", tokens.refreshToken());
        refreshTokenCookie.setHttpOnly(true);
        refreshTokenCookie.setSecure(true);
        refreshTokenCookie.setPath("/");
        refreshTokenCookie.setMaxAge(jwtUtils.getRefreshTokenExpirationSeconds()); // 7일

        // SameSite 속성 설정
        refreshTokenCookie.setAttribute("SameSite", "None");

        // 도메인 설정
        String domain = oAuth2Properties.getCookie().getDomain();
        if (domain != null && !domain.isBlank()) {
            refreshTokenCookie.setDomain(domain);
        }
        response.addCookie(refreshTokenCookie);

        // 3. Access Token은 Response Body에 반환
        LoginResponse loginResponse = new LoginResponse(tokens.accessToken());
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
    public ResponseEntity<LoginedUserProfileResponse> getLoginUserInformation(Authentication authentication, String projectId) {
        SessionPrincipal principal = (SessionPrincipal) authentication.getPrincipal();
        String userId = principal.getKeystoneUserId();

        LoginedUserProfileResponse loginedUserProfileResponse = authServicePort.getUserLoginedProfile(userId, projectId);
        return ResponseEntity.ok(loginedUserProfileResponse);
    }

    @Override
    public ResponseEntity<LogoutResponse> logout(Authentication authentication, HttpServletResponse response) {
        SessionPrincipal principal = (SessionPrincipal) authentication.getPrincipal();
        String userId = principal.getKeystoneUserId();

        // 서버 토큰 무효화
        authServicePort.logout(userId);

        // 쿠키 삭제 (로그인 시 설정한 속성과 동일하게)
        String domain = oAuth2Properties.getCookie().getDomain();

        Cookie refreshTokenCookie = new Cookie("acc-refresh-token", null);
        refreshTokenCookie.setMaxAge(0);
        refreshTokenCookie.setPath("/");
        refreshTokenCookie.setHttpOnly(true);
        refreshTokenCookie.setSecure(true);
        refreshTokenCookie.setAttribute("SameSite", "None");
        if (domain != null && !domain.isBlank()) {
            refreshTokenCookie.setDomain(domain);
        }

        response.addCookie(refreshTokenCookie);

        log.info("logout - 성공 User: {}", userId);
        return ResponseEntity.ok(LogoutResponse.success());
    }
}

