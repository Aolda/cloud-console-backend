package com.acc.local.service.modules.session;

import com.acc.global.exception.session.SessionErrorCode;
import com.acc.global.exception.session.SessionException;
import com.acc.local.domain.model.session.KeycloakTokens;
import com.acc.local.domain.model.session.KeystoneTokens;
import com.acc.local.domain.model.session.ProjectScopedToken;
import com.acc.local.domain.model.session.SessionData;
import com.acc.local.dto.auth.KeystoneToken;
import com.acc.local.external.dto.keycloak.KeycloakTokenResponse;
import com.acc.local.external.ports.KeycloakOidcExternalPort;
import com.acc.local.external.ports.KeystoneAPIExternalPort;
import com.acc.local.repository.ports.SessionRepositoryPort;
import com.acc.local.service.modules.auth.KeystoneTokenModule;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 세션 기반 인증에서 토큰을 조회/갱신하는 모듈.
 *
 * ────────────────────────────────────────────────────────────────
 * [대체 대상]
 *
 * ▶ AuthModule.getUnscopedTokenByUserId(String userId)
 *   - 기존: userId → UserTokenEntity.keystoneUnscopedToken (DB 조회)
 *   - 신규: sessionId → SessionData.keystoneTokens.unscopedToken (Redis 조회)
 *   - 토큰 만료 시: DB에 저장된 keystoneUsername/Password로 Keystone 재발급 + 세션 갱신
 *
 * ▶ AuthModule.issueProjectScopeToken(String projectId, String userId)
 *   - 기존: userId → unscopedToken → Keystone scoped token 발급
 *   - 신규: sessionId → unscopedToken → Keystone scoped token 발급 (getKeystoneScopedToken)
 *
 * ────────────────────────────────────────────────────────────────
 * [설계 원칙]
 *   - 각 토큰은 만료 여부를 검사하고, 만료 시 자동 갱신 후 세션에 업데이트한다.
 *   - Keystone unscoped token 재발급: DB에 저장된 credentials 사용 (password 방식)
 *   - Keycloak access token 재발급:  세션의 refresh_token 사용 (OIDC 방식)
 *   - 세션 자체가 없으면 SESSION_NOT_FOUND 예외 발생 (인증 만료로 처리)
 * ────────────────────────────────────────────────────────────────
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SessionModule {

    private final SessionRepositoryPort sessionRepositoryPort;
    private final KeystoneAPIExternalPort keystoneAPIExternalPort;
    private final KeycloakOidcExternalPort keycloakOidcExternalPort;
    private final KeystoneTokenModule keystoneTokenModule;

    /**
     * 세션에서 Keystone unscoped token을 조회한다. 만료 시 자동 재발급.
     *
     * [대체 대상] AuthModule.getUnscopedTokenByUserId(String userId)
     *   - 기존: userId → UserTokenEntity → keystoneUnscopedToken
     *   - 신규: sessionId → SessionData → keystoneTokens.unscopedToken (만료 시 재발급)
     *
     * @param sessionId 세션 ID (acc-session-id 쿠키 값)
     * @return 유효한 Keystone unscoped token
     * @throws SessionException 세션이 존재하지 않을 경우
     * @throws AuthServiceException 사용자 정보가 없거나 토큰 재발급 실패 시
     */
    public String getKeystoneUnscopedToken(String sessionId) {
        SessionData sessionData = getSessionOrThrow(sessionId);

        KeystoneTokens keystoneTokens = sessionData.getKeystoneTokens();
        if (keystoneTokens != null
                && keystoneTokens.getUnscopedToken() != null
                && !isExpiredOrSoonExpiry(keystoneTokens.getExpiresAt())) {
            return keystoneTokens.getUnscopedToken();
        }

        // 만료: DB credentials로 재발급
        log.debug("Keystone unscoped token 만료 - sessionId: {}, 재발급 시작", sessionId);
        return reissueKeystoneUnscopedToken(sessionId, sessionData);
    }

    /**
     * 세션에서 Keycloak access token을 조회한다. 만료 시 refresh_token으로 자동 갱신.
     *
     * @param sessionId 세션 ID (acc-session-id 쿠키 값)
     * @return 유효한 Keycloak access token
     * @throws SessionException 세션이 존재하지 않을 경우
     */
    public String getKeycloakAccessToken(String sessionId) {
        SessionData sessionData = getSessionOrThrow(sessionId);

        KeycloakTokens keycloakTokens = sessionData.getKeycloakTokens();
        if (keycloakTokens != null
                && keycloakTokens.getAccessToken() != null
                && !isExpiredOrSoonExpiry(keycloakTokens.getExpiresAt())) {
            return keycloakTokens.getAccessToken();
        }

        // 만료: refresh_token으로 갱신
        log.debug("Keycloak access token 만료 - sessionId: {}, 갱신 시작", sessionId);
        return refreshKeycloakTokens(sessionId, sessionData);
    }

    /**
     * 세션에서 Keystone scoped token을 발급한다 (캐시 없음, 매번 발급).
     * 발급된 토큰은 로그아웃 시 폐기할 수 있도록 세션에 추적한다.
     *
     * [대체 대상] AuthModule.issueProjectScopeToken(String projectId, String userId)
     *   - 기존: userId → unscopedToken → getScopedToken(projectId, unscopedToken)
     *   - 신규: sessionId → unscopedToken → getScopedToken(projectId, unscopedToken)
     *
     * @param sessionId 세션 ID
     * @param projectId 스코프 대상 프로젝트 ID
     * @return Keystone scoped token
     */
    public String getKeystoneScopedToken(String sessionId, String projectId) {
        String unscopedToken = getKeystoneUnscopedToken(sessionId);
        KeystoneToken scopedToken = keystoneAPIExternalPort.getScopedToken(projectId, unscopedToken);
        trackScopedToken(sessionId, projectId, unscopedToken, scopedToken);
        return scopedToken.token();
    }

    /**
     * 세션에서 keystoneUserId를 조회한다.
     *
     * @param sessionId 세션 ID
     * @return keystoneUserId (= user_detail.user_id PK)
     */
    public String getKeystoneUserId(String sessionId) {
        return getSessionOrThrow(sessionId).getKeystoneUserId();
    }

    // ─── private ─────────────────────────────────────────────────────────────

    private SessionData getSessionOrThrow(String sessionId) {
        return sessionRepositoryPort.findById(sessionId)
                .orElseThrow(() -> new SessionException(SessionErrorCode.SESSION_NOT_FOUND));
    }

    /**
     * Keystone unscoped token 재발급.
     * DB에 저장된 keystoneUsername + 암호화된 keystonePassword를 사용한다.
     */
    private String reissueKeystoneUnscopedToken(String sessionId, SessionData sessionData) {
        String keystoneUserId = sessionData.getKeystoneUserId();
        KeystoneToken newToken = keystoneTokenModule.issueUnscopedTokenByUserCredentials(keystoneUserId);

        // 세션의 keystoneTokens 갱신
        KeystoneTokens currentTokens = sessionData.getKeystoneTokens();
        KeystoneTokens updatedKeystoneTokens = (currentTokens == null ? KeystoneTokens.builder() : currentTokens.toBuilder())
                .unscopedToken(newToken.token())
                .expiresAt(newToken.expiresAt())
                .build();
        sessionRepositoryPort.updateField(sessionId, "keystoneTokens", updatedKeystoneTokens);

        log.info("Keystone unscoped token 재발급 완료 - sessionId: {}", sessionId);
        return newToken.token();
    }

    /**
     * Keycloak refresh_token으로 access_token 갱신.
     */
    private String refreshKeycloakTokens(String sessionId, SessionData sessionData) {
        String refreshToken = sessionData.getKeycloakTokens().getRefreshToken();
        if (refreshToken == null) {
            throw new SessionException(SessionErrorCode.SESSION_NOT_FOUND);
        }

        KeycloakTokenResponse tokenResponse = keycloakOidcExternalPort.refreshTokens(refreshToken);

        KeycloakTokens updatedKeycloakTokens = KeycloakTokens.builder()
                .accessToken(tokenResponse.accessToken())
                .refreshToken(tokenResponse.refreshToken())
                .idToken(tokenResponse.idToken())
                .expiresAt(LocalDateTime.now().plusSeconds(tokenResponse.expiresIn()))
                .build();
        sessionRepositoryPort.updateField(sessionId, "keycloakTokens", updatedKeycloakTokens);

        log.info("Keycloak access token 갱신 완료 - sessionId: {}", sessionId);
        return tokenResponse.accessToken();
    }

    private void trackScopedToken(String sessionId, String projectId, String unscopedToken, KeystoneToken scopedToken) {
        SessionData sessionData = getSessionOrThrow(sessionId);
        KeystoneTokens currentTokens = sessionData.getKeystoneTokens();
        List<ProjectScopedToken> scopedTokens = new ArrayList<>();
        if (currentTokens != null && currentTokens.getScopedTokens() != null) {
            scopedTokens.addAll(currentTokens.getScopedTokens());
        }
        scopedTokens.add(ProjectScopedToken.builder()
                .projectId(projectId)
                .token(scopedToken.token())
                .expiresAt(scopedToken.expiresAt())
                .build());

        KeystoneTokens updatedTokens = (currentTokens == null ? KeystoneTokens.builder() : currentTokens.toBuilder())
                .unscopedToken(currentTokens != null && currentTokens.getUnscopedToken() != null
                        ? currentTokens.getUnscopedToken()
                        : unscopedToken)
                .scopedToken(scopedToken.token())
                .scopedTokens(scopedTokens)
                .expiresAt(currentTokens != null ? currentTokens.getExpiresAt() : scopedToken.expiresAt())
                .build();

        sessionRepositoryPort.updateField(sessionId, "keystoneTokens", updatedTokens);
    }

    /**
     * 토큰이 만료됐거나 60초 이내로 만료될 경우 true 반환 (조기 갱신 버퍼).
     */
    private boolean isExpiredOrSoonExpiry(LocalDateTime expiresAt) {
        if (expiresAt == null) {
            return true;
        }
        return LocalDateTime.now().plusSeconds(60).isAfter(expiresAt);
    }
}
