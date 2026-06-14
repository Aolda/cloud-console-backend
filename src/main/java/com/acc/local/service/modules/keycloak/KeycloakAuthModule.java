package com.acc.local.service.modules.keycloak;

import com.acc.global.exception.auth.AuthErrorCode;
import com.acc.global.exception.auth.AuthServiceException;
import com.acc.global.exception.session.SessionErrorCode;
import com.acc.global.exception.session.SessionException;
import com.acc.global.properties.KeycloakProperties;
import com.acc.global.security.keycloak.KeycloakIdTokenParser;
import com.acc.global.security.session.SessionConstants;
import com.acc.local.domain.enums.UnivAccountType;
import com.acc.local.domain.model.session.KeycloakTokens;
import com.acc.local.domain.model.session.KeystoneTokens;
import com.acc.local.domain.model.session.ProjectScopedToken;
import com.acc.local.domain.model.session.SessionData;
import com.acc.local.domain.model.session.UserInfo;
import com.acc.local.dto.auth.KeycloakIdTokenClaims;
import com.acc.local.dto.auth.KeycloakUserResult;
import com.acc.local.dto.auth.KeystonePasswordLoginRequest;
import com.acc.local.dto.auth.KeystoneToken;
import com.acc.local.dto.auth.UserDepartDto;
import com.acc.local.external.dto.keycloak.KeycloakTokenResponse;
import com.acc.local.external.ports.KeycloakOidcExternalPort;
import com.acc.local.external.ports.KeystoneAPIExternalPort;
import com.acc.local.repository.ports.SessionRepositoryPort;
import com.acc.local.service.modules.auth.AjouUnivModule;
import com.acc.local.service.modules.auth.KeystoneTokenModule;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * Keycloak OIDC 인증 오케스트레이션 모듈 (서비스 레이어).
 *
 * ────────────────────────────────────────────────────────────────
 * [processCallback() 처리 흐름]
 *
 *  1. Keycloak에서 code → TokenResponse 교환          (External 계층)
 *  2. ID Token 파싱 → 사용자 클레임 추출              (KeycloakIdTokenParser)
 *  3. 사용자 조회/등록 3-way 분기                     (KeycloakUserModule)
 *       ├ keycloakUserId 연결됨  → 기존 로그인
 *       ├ email 일치             → 계정 연결 (Keystone 패스워드 재설정)
 *       └ 없음                  → Keystone 신규 생성 + DB 저장
 *  4. Keystone unscoped token 발급                    (KeystoneAPIExternalPort)
 *  5. SessionData 완전체 구성 → Redis 저장            (SessionRepositoryPort)
 *  6. sessionId 반환 → 컨트롤러에서 쿠키 설정         (KeycloakAuthController)
 * ────────────────────────────────────────────────────────────────
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class KeycloakAuthModule {

    // 외부 모듈의존
    private final KeycloakOidcExternalPort keycloakOidcExternalPort;
    private final KeystoneAPIExternalPort keystoneAPIExternalPort;
    private final SessionRepositoryPort sessionRepositoryPort;

    // 내부 모듈 의존
    private final KeycloakUserModule keycloakUserModule;
    private final AjouUnivModule ajouUnivModule;
    private final KeystoneTokenModule keystoneTokenModule;

    // Token util
    private final KeycloakIdTokenParser keycloakIdTokenParser;

    private final KeycloakProperties keycloakProperties;

    /** 세션 유효성 확인 */
    public boolean isSessionValid(String sessionId) {
        return sessionRepositoryPort.findById(sessionId).isPresent();
    }

    /** Keycloak 인가 URL 생성 */
    public String buildAuthorizationUrl(String state) {
        return keycloakOidcExternalPort.getAuthorizationUrl(state, keycloakProperties.getRedirectUri());
    }

    /**
     * Keycloak OIDC 콜백 처리: code → 세션 생성 → sessionId 반환.
     */
    public String processCallback(String code) {

        // 1. code → Keycloak 토큰 교환
        KeycloakTokenResponse tokenResponse =
                keycloakOidcExternalPort.exchangeCodeForTokens(code, keycloakProperties.getRedirectUri());

        // 2. ID Token에서 모든 필요 클레임 추출
        KeycloakIdTokenClaims claims = keycloakIdTokenParser.extractClaims(tokenResponse.idToken());

        // 2.5. 그룹 기반 admin 여부 판별 및 학적 정보 조회 (재학생 검증은 Keycloak에서 처리)
        boolean isAdminByGroup = claims.groups().contains(keycloakProperties.getAdminGroupPath());
        UserDepartDto departDto = ajouUnivModule.getUserDepartInfoFromKeycloakClaims(claims)
                .orElseThrow(() -> {
                    log.warn("Keycloak 로그인 실패 - 학적 정보 없음: {}", claims.email());
                    return new AuthServiceException(AuthErrorCode.NO_UNIV_ACCOUNT_INFO);
                });

        // 3. 사용자 조회/등록 (KeycloakUserModule) — admin 여부도 함께 전달
        KeycloakUserResult userResult = keycloakUserModule.findOrRegisterKeycloakUser(claims, departDto, isAdminByGroup);

        // 4. KeycloakTokens 도메인 모델 구성
        KeycloakTokens keycloakTokens = KeycloakTokens.builder()
                .accessToken(tokenResponse.accessToken())
                .refreshToken(tokenResponse.refreshToken())
                .idToken(tokenResponse.idToken())
                .expiresAt(LocalDateTime.now().plusSeconds(tokenResponse.expiresIn()))
                .build();

        // 5. Keystone unscoped token 발급 (저장된 credentials 사용)
        //    DB에 저장된 keystoneUsername/Password로 직접 발급
        KeystonePasswordLoginRequest keystoneLoginRequest = new KeystonePasswordLoginRequest(
                userResult.keystoneUsername(),
                userResult.keystonePlainPassword(),
                "Default"
        );
        KeystoneToken keystoneToken = keystoneAPIExternalPort.getUnscopedToken(keystoneLoginRequest);

        KeystoneTokens keystoneTokens = KeystoneTokens.builder()
                .unscopedToken(keystoneToken.token())
                .expiresAt(keystoneToken.expiresAt())
                .build();

        // 6. SessionData 완전체 구성
        UserInfo userInfo = UserInfo.builder()
                .name(userResult.userName())
                .email(claims.email())
                .build();

        SessionData sessionData = SessionData.builder()
                .keycloakTokens(keycloakTokens)
                .keystoneTokens(keystoneTokens)
                .keycloakUserId(claims.subject())
                .keystoneUserId(userResult.keystoneUserId())
                .userInfo(userInfo)
                .build();

        // 7. Redis 세션 저장
        String sessionId = UUID.randomUUID().toString();
        sessionRepositoryPort.save(
                sessionId, sessionData,
                SessionConstants.DEFAULT_SESSION_TTL_MINUTES, TimeUnit.MINUTES
        );

        log.info("Keycloak OIDC 로그인 완료 - keycloakUserId={}, keystoneUserId={}",
                claims.subject(), userResult.keystoneUserId());

        return sessionId;
    }

    /**
     * Keycloak 로그아웃 처리: Keycloak refresh_token/Keystone token 폐기 → Redis 세션 삭제.
     *
     * [처리 순서]
     *   1. sessionId로 Redis에서 SessionData 조회
     *   2. SessionData.keycloakTokens.refreshToken → Keycloak Token Revocation API 호출
     *   3. SessionData.keystoneTokens → Keystone Token Revocation API 호출
     *   4. Redis에서 세션 삭제
     *
     * [Keycloak 폐기 실패 처리]
     *   Keycloak 서버가 일시 불가해도 로컬 세션은 반드시 삭제.
     *   (Keycloak refresh_token 폐기 실패는 경고 로그로만 처리)
     *
     * @param sessionId 로그아웃할 세션 ID (acc-session-id 쿠키 값)
     * @throws SessionException 세션이 존재하지 않을 경우
     */
    public void logout(String sessionId) {
        SessionData sessionData = sessionRepositoryPort.findById(sessionId)
                .orElseThrow(() -> new SessionException(SessionErrorCode.SESSION_NOT_FOUND));

        // Keycloak refresh_token 폐기 (실패해도 로컬 세션 삭제 진행)
        try {
            if (sessionData.getKeycloakTokens() != null
                    && sessionData.getKeycloakTokens().getRefreshToken() != null) {
                keycloakOidcExternalPort.revokeToken(sessionData.getKeycloakTokens().getRefreshToken());
            }
        } catch (Exception e) {
            log.warn("Keycloak 토큰 폐기 실패 (세션 삭제는 진행) - sessionId: {}", sessionId, e);
        }

        revokeKeystoneTokens(sessionData.getKeystoneTokens());

        sessionRepositoryPort.deleteById(sessionId);
        log.info("Keycloak 로그아웃 완료 - sessionId: {}", sessionId);
    }

    private void revokeKeystoneTokens(KeystoneTokens keystoneTokens) {
        if (keystoneTokens == null) {
            return;
        }

        Set<String> tokens = new LinkedHashSet<>();
        tokens.add(keystoneTokens.getUnscopedToken());
        tokens.add(keystoneTokens.getScopedToken());
        if (keystoneTokens.getScopedTokens() != null) {
            for (ProjectScopedToken scopedToken : keystoneTokens.getScopedTokens()) {
                if (scopedToken != null) {
                    tokens.add(scopedToken.getToken());
                }
            }
        }

        for (String token : tokens) {
            keystoneTokenModule.revokeTokenQuietly(token);
        }
    }
}
