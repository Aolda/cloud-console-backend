package com.acc.local.domain.enums.auth;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 인증 타입
 * - GOOGLE: Google 인증 (0)
 * - GITLAB: GitLab 인증 (1)
 */
@Getter
@RequiredArgsConstructor
public enum AuthType {
    GOOGLE(0, "Google 인증"),
    GITLAB(1, "GitLab 인증");

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
}
