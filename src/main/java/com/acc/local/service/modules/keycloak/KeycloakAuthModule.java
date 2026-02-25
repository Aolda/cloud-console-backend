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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * Keycloak OIDC 인증 오케스트레이션 모듈 (서비스 레이어).
 *
 * ────────────────────────────────────────────────────────────────
 * [대체 대상]
 *
 * ▶ AuthModule.authenticateAndGenerateJwt()  (Keycloak Federate 방식)
 *   - 기존: Keycloak access_token → KeystoneAPIExternalPort.requestFederateLogin()
 *           → Keystone Federate 토큰 발급 → JWT(ACC 서비스 토큰) 발급
 *   - 신규: Authorization Code Flow → ID Token 클레임 추출
 *           → Keystone 직접 password 로그인 → Redis 세션 생성
 *   - 완전 전환 후 requestFederateLogin(), JwtUtils, UserTokenEntity 제거 가능
 *
 * ▶ AuthModule.authenticateKeystoneAndGenerateJwt()  (Keystone 패스워드 로그인)
 *   - 기존: username/password → Keystone unscoped token → JWT 발급
 *   - 신규: 위 흐름은 내부적으로 유지되지만, 사용자는 Keycloak으로만 진입
 *           (keystoneUsername + keystonePlainPassword는 우리 DB에서 관리)
 *
 * ▶ OAuthSuccessHandler, JwtAuthenticationFilter (JWT 기반 세션)
 *   - 기존: Set-Cookie(JWT) → 매 요청마다 JwtAuthenticationFilter 검증
 *   - 신규: Set-Cookie(acc-session-id) → Redis 세션 조회로 대체
 *   - Step 4(세션 미들웨어)에서 JwtAuthenticationFilter를 SessionAuthFilter로 교체
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

    // Token util
    private final KeycloakIdTokenParser keycloakIdTokenParser;

    private final KeycloakProperties keycloakProperties;

    /** Keycloak 인가 URL 생성 */
    public String buildAuthorizationUrl(String state) {
        return keycloakOidcExternalPort.getAuthorizationUrl(state, keycloakProperties.getRedirectUri());
    }

    /**
     * Keycloak OIDC 콜백 처리: code → 세션 생성 → sessionId 반환.
     *
     * [이전 구현 vs 현재]
     *   이전: keycloakUserId만 추출 → SessionData(keycloakTokens만 채워진 반쪽 세션)
     *   현재: 클레임 전체 추출 → 사용자 등록/연결 → Keystone 토큰 발급 → 완전한 SessionData
     */
    public String processCallback(String code) {

        // 1. code → Keycloak 토큰 교환
        KeycloakTokenResponse tokenResponse =
                keycloakOidcExternalPort.exchangeCodeForTokens(code, keycloakProperties.getRedirectUri());

        // 2. ID Token에서 모든 필요 클레임 추출
        KeycloakIdTokenClaims claims = keycloakIdTokenParser.extractClaims(tokenResponse.idToken());

        // 2.5. 학적 정보 추출 및 재학생 검증 (관리자 계정은 우회)
        UserDepartDto departDto = null;
        if (!keycloakUserModule.isLinkedAdmin(claims.subject())) {
            departDto = ajouUnivModule.getUserDepartInfoFromKeycloakClaims(claims)
                    .orElseThrow(() -> {
                        log.warn("Keycloak 로그인 실패 - 학적 정보 없음: {}", claims.email());
                        return new AuthServiceException(AuthErrorCode.NO_UNIV_ACCOUNT_INFO);
                    });
            if (departDto.univAccountType() != UnivAccountType.UNDERGRADUATE) {
                log.warn("Keycloak 로그인 실패 - 재학생 아님: {}, type={}", claims.email(), departDto.univAccountType());
                throw new AuthServiceException(AuthErrorCode.ONLY_UNDERGRADUATE_ALLOWED);
            }
        }

        // 3. 사용자 조회/등록 (3-way 분기: KeycloakUserModule)
        KeycloakUserResult userResult = keycloakUserModule.findOrRegisterKeycloakUser(claims, departDto);

        // 4. KeycloakTokens 도메인 모델 구성
        KeycloakTokens keycloakTokens = KeycloakTokens.builder()
                .accessToken(tokenResponse.accessToken())
                .refreshToken(tokenResponse.refreshToken())
                .idToken(tokenResponse.idToken())
                .expiresAt(LocalDateTime.now().plusSeconds(tokenResponse.expiresIn()))
                .build();

        // 5. Keystone unscoped toke                     n 발급 (저장된 credentials 사용)
        //    [대체 대상] AuthModule.getUnscopedTokenByUserId() → UserTokenEntity 조회 방식
        //                대신 DB에 저장된 keystoneUsername/Password로 직접 발급
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
     * Keycloak 로그아웃 처리: Keycloak refresh_token 폐기 → Redis 세션 삭제.
     *
     * [처리 순서]
     *   1. sessionId로 Redis에서 SessionData 조회
     *   2. SessionData.keycloakTokens.refreshToken → Keycloak Token Revocation API 호출
     *   3. Redis에서 세션 삭제
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

        sessionRepositoryPort.deleteById(sessionId);
        log.info("Keycloak 로그아웃 완료 - sessionId: {}", sessionId);
    }
}
