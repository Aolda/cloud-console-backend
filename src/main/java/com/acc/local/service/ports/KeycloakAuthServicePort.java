package com.acc.local.service.ports;

public interface KeycloakAuthServicePort {

    /**
     * Keycloak 인가 URL 생성.
     * redirectUri는 서비스 레이어가 KeycloakProperties에서 직접 참조한다.
     *
     * @param state CSRF 방지용 state 파라미터
     * @return Keycloak Authorization Endpoint URL
     */
    String buildAuthorizationUrl(String state);

    /**
     * Keycloak OIDC 콜백 처리: code → 토큰 교환 → 세션 생성.
     * redirectUri는 서비스 레이어가 KeycloakProperties에서 직접 참조한다.
     *
     * @param code Keycloak이 callback URL에 전달한 인가코드
     * @return 생성된 sessionId
     */
    String processCallback(String code);

    /**
     * Keycloak 로그아웃 처리: refresh_token 폐기 → Redis 세션 삭제
     *
     * @param sessionId 로그아웃할 세션 ID (acc-session-id 쿠키 값)
     */
    void logout(String sessionId);
}
