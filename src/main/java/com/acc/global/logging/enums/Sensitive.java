package com.acc.global.logging.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Sensitive {

    KEY("키값"),
    PASSWORD("비밀번호"),
    TOKEN("토큰값"),
    SECRET("시크릿값"),
    CREDENTIAL("자격증명값");

    private final String description;

    public static final String SENSITIVES_REGEX = "(key|password|token|secret|credential)";

    public static boolean isSensitive(String fieldName) {
        for (Sensitive sensitive : Sensitive.values()) {
            if (fieldName.toUpperCase().contains(sensitive.name())) {
                return true;
            }
        }
        return false;
    }

}
