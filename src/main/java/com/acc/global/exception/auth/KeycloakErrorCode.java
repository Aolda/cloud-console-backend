package com.acc.global.exception.auth;

import com.acc.global.exception.ErrorCode;
import lombok.Getter;

@Getter
public enum KeycloakErrorCode implements ErrorCode {

    // 400 Bad Request
    KEYCLOAK_INVALID_GRANT(400, "ACC-KEYCLOAK-INVALID-GRANT", "Keycloak 인증 코드가 유효하지 않습니다."),
    KEYCLOAK_INVALID_STATE(400, "ACC-KEYCLOAK-INVALID-STATE", "CSRF state 값이 일치하지 않습니다."),
    KEYCLOAK_MISSING_IDP_TYPE(400, "ACC-KEYCLOAK-MISSING-IDP-TYPE", "Keycloak auth_idp_type 클레임이 존재하지 않습니다."),
    KEYCLOAK_UNSUPPORTED_IDP_TYPE(400, "ACC-KEYCLOAK-UNSUPPORTED-IDP-TYPE", "지원하지 않는 auth_idp_type 값입니다."),

    // 401 Unauthorized
    KEYCLOAK_UNAUTHORIZED(401, "ACC-KEYCLOAK-UNAUTHORIZED", "Keycloak 인증에 실패했습니다."),

    // 500 Internal Server Error
    KEYCLOAK_TOKEN_EXCHANGE_FAILED(500, "ACC-KEYCLOAK-TOKEN-EXCHANGE-FAILED", "Keycloak 토큰 교환에 실패했습니다."),
    KEYCLOAK_TOKEN_REFRESH_FAILED(500, "ACC-KEYCLOAK-TOKEN-REFRESH-FAILED", "Keycloak 토큰 갱신에 실패했습니다."),
    KEYCLOAK_TOKEN_INTROSPECT_FAILED(500, "ACC-KEYCLOAK-TOKEN-INTROSPECT-FAILED", "Keycloak 토큰 검증에 실패했습니다."),
    KEYCLOAK_ID_TOKEN_PARSE_FAILED(500, "ACC-KEYCLOAK-ID-TOKEN-PARSE-FAILED", "Keycloak ID Token 파싱에 실패했습니다."),
    KEYCLOAK_TOKEN_REVOKE_FAILED(500, "ACC-KEYCLOAK-TOKEN-REVOKE-FAILED", "Keycloak 토큰 폐기에 실패했습니다."),
    KEYCLOAK_API_FAILURE(500, "ACC-KEYCLOAK-API-FAILURE", "Keycloak API 통신 중 오류가 발생하였습니다.");

    private final int status;
    private final String code;
    private final String message;

    KeycloakErrorCode(int status, String code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }
}
