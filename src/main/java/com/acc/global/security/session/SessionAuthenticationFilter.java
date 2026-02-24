package com.acc.global.security.session;

import com.acc.global.security.session.SessionConstants;
import com.acc.local.domain.model.session.SessionData;
import com.acc.local.repository.ports.SessionRepositoryPort;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * Keycloak OIDC 세션 기반 인증 필터.
 *
 * [대체 대상] JwtAuthenticationFilter (JWT 기반 인증)
 *   - 기존: Authorization: Bearer {jwt} → JwtInfo(userId) → SecurityContext
 *   - 신규: Cookie: acc-session-id={uuid} → SessionPrincipal(sessionId, keycloakUserId, keystoneUserId) → SecurityContext
 *
 * [공존 전략 - Phase 1]
 *   JwtAuthenticationFilter → SessionAuthenticationFilter 순서로 실행된다.
 *   - JWT Bearer Token이 있으면 JwtAuthenticationFilter가 먼저 인증 처리
 *   - 세션 쿠키만 있으면 이 필터가 인증 처리
 *   - 둘 다 없으면 미인증 상태 유지 (기존과 동일)
 *   → 기존 API는 기존 방식 그대로 동작. Keycloak 사용자만 세션 방식 사용.
 *
 * [Sliding Session]
 *   요청이 들어올 때마다 TTL을 연장한다. (30분 sliding window)
 *   활성 사용자의 세션이 중간에 만료되는 것을 방지.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SessionAuthenticationFilter extends OncePerRequestFilter {

    private final SessionRepositoryPort sessionRepositoryPort;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        // 이미 JWT 인증이 완료된 경우 세션 인증을 시도하지 않는다
        if (SecurityContextHolder.getContext().getAuthentication() != null) {
            filterChain.doFilter(request, response);
            return;
        }

        String sessionId = extractSessionId(request);

        if (sessionId != null) {
            authenticate(request, sessionId);
        }

        filterChain.doFilter(request, response);
    }

    private void authenticate(HttpServletRequest request, String sessionId) {
        try {
            Optional<SessionData> sessionDataOpt = sessionRepositoryPort.findById(sessionId);

            if (sessionDataOpt.isEmpty()) {
                log.debug("세션 미존재 또는 만료 - sessionId: {}", sessionId);
                return;
            }

            SessionData sessionData = sessionDataOpt.get();

            SessionPrincipal principal = new SessionPrincipal(
                    sessionId,
                    sessionData.getKeycloakUserId(),
                    sessionData.getKeystoneUserId()
            );

            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(principal, null, Collections.emptyList());
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            SecurityContextHolder.getContext().setAuthentication(authentication);

            // Sliding Session: 요청마다 TTL 연장
            sessionRepositoryPort.extendTtl(
                    sessionId,
                    SessionConstants.DEFAULT_SESSION_TTL_MINUTES,
                    TimeUnit.MINUTES
            );

            MDC.put("sessionId", sessionId);
            MDC.put("keystoneUserId", sessionData.getKeystoneUserId());
            log.debug("세션 인증 성공 - keystoneUserId: {}", sessionData.getKeystoneUserId());

        } catch (Exception e) {
            // 세션 조회 실패는 인증 실패로 처리 (미인증 상태 유지)
            SecurityContextHolder.clearContext();
            log.warn("세션 인증 처리 중 오류 발생 - sessionId: {}", sessionId, e);
        }
    }

    private String extractSessionId(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return null;
        }
        return Arrays.stream(cookies)
                .filter(c -> SessionConstants.SESSION_COOKIE_NAME.equals(c.getName()))
                .map(Cookie::getValue)
                .findFirst()
                .orElse(null);
    }
}
