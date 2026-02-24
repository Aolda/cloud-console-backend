package com.acc.global.security.session;

/**
 * 세션 관련 상수 정의.
 *
 * [위치 근거] SESSION_COOKIE_NAME 등 HTTP/보안 계층 상수는 global/security/ 에 위치해야 한다.
 *            global 패키지가 local 패키지를 참조하면 의존성 방향이 역전되므로
 *            SessionAuthenticationFilter(global)가 참조할 수 있도록 global 계층으로 이동.
 *
 * local 계층(RedisSessionRepositoryAdapter 등)은 global → local 방향으로 정상 참조 가능.
 */
public final class SessionConstants {

    private SessionConstants() {
    }

    public static final String SESSION_KEY_PREFIX = "session:";
    public static final String FIELD_KEYCLOAK_TOKENS = "keycloakTokens";
    public static final String FIELD_KEYSTONE_TOKENS = "keystoneTokens";
    public static final String FIELD_KEYCLOAK_USER_ID = "keycloakUserId";
    public static final String FIELD_KEYSTONE_USER_ID = "keystoneUserId";
    public static final String FIELD_USER_INFO = "userInfo";
    public static final long DEFAULT_SESSION_TTL_MINUTES = 30;
    public static final String SESSION_COOKIE_NAME = "acc-session-id";
}
