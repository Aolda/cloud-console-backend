package com.acc.local.controller;

import com.acc.global.exception.auth.KeycloakErrorCode;
import com.acc.global.exception.auth.KeycloakException;
import com.acc.global.exception.session.SessionErrorCode;
import com.acc.global.exception.session.SessionException;
import com.acc.global.properties.KeycloakProperties;
import com.acc.global.properties.OAuth2Properties;
import com.acc.global.security.session.SessionConstants;
import com.acc.local.controller.docs.KeycloakAuthDocs;
import com.acc.local.service.ports.KeycloakAuthServicePort;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.Arrays;
import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
public class KeycloakAuthController implements KeycloakAuthDocs {

    private static final String STATE_COOKIE_NAME = "keycloak-oauth-state";

    private final KeycloakAuthServicePort keycloakAuthServicePort;
    private final KeycloakProperties keycloakProperties;
    private final OAuth2Properties oAuth2Properties;

    @Override
    public void login(HttpServletResponse response) {
        String state = UUID.randomUUID().toString();

        // state 쿠키 설정 (HttpOnly, Secure, SameSite=Lax, maxAge=300s)
        Cookie stateCookie = new Cookie(STATE_COOKIE_NAME, state);
        stateCookie.setHttpOnly(true);
        stateCookie.setSecure(true);
        stateCookie.setPath("/api/v1/auth/keycloak/callback");
        stateCookie.setMaxAge(300);
        response.addCookie(stateCookie);
        response.setHeader("Set-Cookie",
                response.getHeader("Set-Cookie") + "; SameSite=Lax");

        String authorizationUrl = keycloakAuthServicePort.buildAuthorizationUrl(state);

        try {
            response.sendRedirect(authorizationUrl);
        } catch (IOException e) {
            log.error("Keycloak 로그인 리다이렉트 실패", e);
            throw new KeycloakException(KeycloakErrorCode.KEYCLOAK_API_FAILURE, e);
        }
    }

    @Override
    public void callback(
            @RequestParam("code") String code,
            @RequestParam("state") String state,
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        // 1. state 쿠키에서 값 추출
        String cookieState = extractCookieValue(request, STATE_COOKIE_NAME);

        // 2. state 검증
        if (cookieState == null || !cookieState.equals(state)) {
            throw new KeycloakException(KeycloakErrorCode.KEYCLOAK_INVALID_STATE);
        }

        // 3. state 쿠키 삭제
        Cookie deleteStateCookie = new Cookie(STATE_COOKIE_NAME, "");
        deleteStateCookie.setHttpOnly(true);
        deleteStateCookie.setSecure(true);
        deleteStateCookie.setPath("/api/v1/auth/keycloak/callback");
        deleteStateCookie.setMaxAge(0);
        response.addCookie(deleteStateCookie);

        // 4. 콜백 처리 (토큰 교환 + 세션 생성)
        String sessionId = keycloakAuthServicePort.processCallback(code);

        // 5. 세션 쿠키 설정 (HttpOnly, Secure, SameSite=None, maxAge=30분)
        Cookie sessionCookie = new Cookie(SessionConstants.SESSION_COOKIE_NAME, sessionId);
        sessionCookie.setHttpOnly(true);
        sessionCookie.setSecure(true);
        sessionCookie.setPath("/");
        sessionCookie.setMaxAge((int) SessionConstants.DEFAULT_SESSION_TTL_MINUTES * 60);
        if (oAuth2Properties.getCookie() != null && oAuth2Properties.getCookie().getDomain() != null) {
            sessionCookie.setDomain(oAuth2Properties.getCookie().getDomain());
        }
        response.addCookie(sessionCookie);

        // SameSite=None 설정 (Servlet API에서 직접 지원하지 않으므로 헤더 조작)
        String sessionCookieHeader = String.format(
                "%s=%s; Path=/; Max-Age=%d; HttpOnly; Secure; SameSite=None",
                SessionConstants.SESSION_COOKIE_NAME,
                sessionId,
                (int) SessionConstants.DEFAULT_SESSION_TTL_MINUTES * 60
        );
        if (oAuth2Properties.getCookie() != null && oAuth2Properties.getCookie().getDomain() != null) {
            sessionCookieHeader += "; Domain=" + oAuth2Properties.getCookie().getDomain();
        }
        response.setHeader("Set-Cookie", sessionCookieHeader);

        // 6. 프론트엔드로 리다이렉트
        try {
            response.sendRedirect(keycloakProperties.getFrontendRedirectUrl());
        } catch (IOException e) {
            log.error("프론트엔드 리다이렉트 실패", e);
            throw new KeycloakException(KeycloakErrorCode.KEYCLOAK_API_FAILURE, e);
        }
    }

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response) {
        String sessionId = extractCookieValue(request, SessionConstants.SESSION_COOKIE_NAME);

        if (sessionId == null) {
            throw new SessionException(SessionErrorCode.SESSION_NOT_FOUND);
        }

        // 1. 로그아웃 처리 (Keycloak refresh_token 폐기 + Redis 세션 삭제)
        keycloakAuthServicePort.logout(sessionId);

        // 2. acc-session-id 쿠키 삭제
        String deleteCookieHeader = String.format(
                "%s=; Path=/; Max-Age=0; HttpOnly; Secure; SameSite=None",
                SessionConstants.SESSION_COOKIE_NAME
        );
        if (oAuth2Properties.getCookie() != null && oAuth2Properties.getCookie().getDomain() != null) {
            deleteCookieHeader += "; Domain=" + oAuth2Properties.getCookie().getDomain();
        }
        response.setHeader("Set-Cookie", deleteCookieHeader);
    }

    private String extractCookieValue(HttpServletRequest request, String cookieName) {
        if (request.getCookies() == null) {
            return null;
        }
        return Arrays.stream(request.getCookies())
                .filter(c -> cookieName.equals(c.getName()))
                .map(Cookie::getValue)
                .findFirst()
                .orElse(null);
    }
}
