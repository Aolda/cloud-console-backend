package com.acc.global.security.session;

import com.acc.local.domain.model.session.KeycloakTokens;
import com.acc.local.domain.model.session.SessionData;
import com.acc.local.repository.ports.SessionRepositoryPort;
import com.acc.local.service.modules.session.SessionModule;
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
import java.time.LocalDateTime;
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
 * [인증 단계]
 *   1. acc-session-id 쿠키 추출
 *   2. Redis에서 SessionData 조회 (없으면 미인증)
 *   3. Keycloak accessToken 유효성 검사 (로컬 timestamp 체크 → HTTP 없음)
 *      - 만료 시: refreshToken으로 Keycloak 갱신 시도 (HTTP 1회, ~accessToken 수명마다)
 *      - 갱신 실패(refreshToken 만료): Redis 세션 삭제 → 미인증 (재로그인 유도)
 *   4. SecurityContext에 SessionPrincipal 등록
 *   5. Sliding Session TTL 연장 (30분)
 *
 * [세션 만료 정책]
 *   - Redis TTL 소진(30분 무요청): 세션 만료 → 401
 *   - Keycloak refreshToken 만료: Redis 세션 삭제 → 401 (Keycloak 서버 설정에 따라 제어됨)
 *   - 활성 사용자의 Redis TTL은 계속 연장되지만, Keycloak refreshToken이 만료되면 강제 종료
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SessionAuthenticationFilter extends OncePerRequestFilter {

    private final SessionRepositoryPort sessionRepositoryPort;
    private final SessionModule sessionModule;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

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

            // Keycloak 토큰 유효성 검사 및 갱신
            // - accessToken 유효: 로컬 timestamp 체크만 수행 (HTTP 없음, 대부분의 요청)
            // - accessToken 만료: refreshToken으로 Keycloak 갱신 (HTTP 1회, ~수명마다)
            // - refreshToken까지 만료: Redis 세션 삭제 → 재로그인 유도
            if (!ensureKeycloakTokenValid(sessionId, sessionData)) {
                log.info("Keycloak 세션 만료 - Redis 세션 삭제, 재로그인 필요 - sessionId: {}", sessionId);
                sessionRepositoryPort.deleteById(sessionId);
                return;
            }

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
            SecurityContextHolder.clearContext();
            log.warn("세션 인증 처리 중 오류 발생 - sessionId: {}", sessionId, e);
        }
    }

    /**
     * Keycloak accessToken 유효성 확인 및 필요 시 갱신.
     *
     * - accessToken 유효: 로컬 체크만, HTTP 없음
     * - accessToken 만료: SessionModule.getKeycloakAccessToken() 호출 → Keycloak refresh HTTP 1회
     * - refreshToken 만료 or Keycloak 오류: false 반환 → 호출부에서 세션 삭제
     *
     * @return true: 세션 계속 사용 가능 / false: Keycloak 세션 만료 → 재로그인 필요
     */
    private boolean ensureKeycloakTokenValid(String sessionId, SessionData sessionData) {
        KeycloakTokens keycloakTokens = sessionData.getKeycloakTokens();

        if (keycloakTokens == null) {
            return false;
        }

        // accessToken 아직 유효 → HTTP 호출 없이 통과 (대부분의 요청)
        if (!isExpiredOrSoonExpiry(keycloakTokens.getExpiresAt())) {
            return true;
        }

        // accessToken 만료 → refreshToken으로 갱신 시도
        try {
            sessionModule.getKeycloakAccessToken(sessionId);
            log.debug("Keycloak accessToken 갱신 완료 - sessionId: {}", sessionId);
            return true;
        } catch (Exception e) {
            log.info("Keycloak 토큰 갱신 실패 (refreshToken 만료 또는 Keycloak 불가) - sessionId: {}", sessionId);
            return false;
        }
    }

    /**
     * 토큰이 만료됐거나 60초 이내로 만료될 경우 true (조기 갱신 버퍼).
     */
    private boolean isExpiredOrSoonExpiry(LocalDateTime expiresAt) {
        if (expiresAt == null) return true;
        return LocalDateTime.now().plusSeconds(60).isAfter(expiresAt);
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
