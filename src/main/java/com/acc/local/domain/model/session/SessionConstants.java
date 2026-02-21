package com.acc.local.domain.model.session;

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
}
