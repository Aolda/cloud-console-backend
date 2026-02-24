package com.acc.local.external.ports;

import com.acc.local.external.dto.keycloak.KeycloakIntrospectResponse;
import com.acc.local.external.dto.keycloak.KeycloakTokenResponse;

public interface KeycloakOidcExternalPort {

    /**
     * Keycloak 로그인 페이지 URL 생성
     * 프론트엔드를 이 URL로 리다이렉트시키면 Keycloak 로그인 화면이 표시된다.
     * @param state CSRF 방지용 state 파라미터
     * @return Keycloak Authorization Endpoint URL (쿼리 파라미터 포함)
     */
    String getAuthorizationUrl(String state, String redirectUri);

    /**
     * Keycloak 인가코드를 토큰으로 교환 (Authorization Code Flow)
     * 사용자가 Keycloak 로그인 후 callback으로 전달받은 code를 사용한다.
     * @param code Keycloak이 callback URL에 전달한 인가코드
     * @param redirectUri 토큰 교환 시 사용할 redirect_uri (인가 요청 때와 동일해야 함)
     * @return access_token, refresh_token, id_token 포함 응답
     */
    KeycloakTokenResponse exchangeCodeForTokens(String code, String redirectUri);

    /**
     * Keycloak refresh_token으로 새 토큰 발급
     * access_token 만료 시 재로그인 없이 토큰을 갱신한다.
     * @param refreshToken Keycloak에서 발급받은 refresh_token
     * @return 새로운 access_token, refresh_token, id_token 포함 응답
     */
    KeycloakTokenResponse refreshTokens(String refreshToken);

    /**
     * Keycloak 토큰 유효성 검증 (Token Introspection)
     * access_token이 아직 유효한지 Keycloak 서버에 확인한다.
     * @param token 검증할 access_token
     * @return active 여부, 만료시각, 사용자 정보 포함 응답
     */
    KeycloakIntrospectResponse introspectToken(String token);

    /**
     * Keycloak 토큰 폐기 (Token Revocation)
     * refresh_token 또는 access_token을 Keycloak 서버에서 폐기한다.
     * @param token 폐기할 토큰
     */
    void revokeToken(String token);
}
