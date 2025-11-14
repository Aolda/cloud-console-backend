package com.acc.local.domain.enums.auth;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 약관 타입
 */
@Getter
@RequiredArgsConstructor
public enum TermType {
    SERVICE("SERVICE", "서비스 이용약관");

    private final String code;
    private final String description;

    public static TermType fromCode(String code) {
        for (TermType type : values()) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Invalid TermType code: " + code);
    }
}