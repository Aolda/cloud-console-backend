package com.acc.local.domain.enums.auth;

import com.acc.global.exception.auth.KeycloakErrorCode;
import com.acc.global.exception.auth.KeycloakException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 인증 타입
 * - GOOGLE: Google 인증 (0)
 * - GITLAB: GitLab 인증 (1)
 * - ADMIN:  관리자 직접 생성 (2)
 *
 * Keycloak custom attribute "auth_idp_type" 값(google/gitlab)으로 결정된다.
 */
@Getter
@RequiredArgsConstructor
public enum AuthType {
    GOOGLE(0, "Google 인증"),
    GITLAB(1, "GitLab 인증"),
    ADMIN(2, "관리자 직접 생성");

    private final int code;
    private final String description;

    public static AuthType fromCode(int code) {
        for (AuthType type : values()) {
            if (type.getCode() == code) {
                return type;
            }
        }
        throw new IllegalArgumentException("Invalid AuthType code: " + code);
    }

    /** Keycloak "auth_idp_type" 클레임 값 → AuthType 변환 */
    public static AuthType fromIdpType(String idpType) {
        if (idpType == null || idpType.isBlank()) {
            throw new KeycloakException(KeycloakErrorCode.KEYCLOAK_MISSING_IDP_TYPE);
        }
        return switch (idpType.toLowerCase()) {
            case "google" -> GOOGLE;
            case "gitlab" -> GITLAB;
            default -> throw new KeycloakException(
                    KeycloakErrorCode.KEYCLOAK_UNSUPPORTED_IDP_TYPE,
                    "지원하지 않는 auth_idp_type: " + idpType
            );
        };
    }
}
