package com.acc.global.exception.auth;

import com.acc.global.exception.ErrorCode;
import lombok.Getter;

/**
 * Auth 에러코드
 */
@Getter
public enum AuthErrorCode implements ErrorCode {

    FAILED_TOKEN_AUTHENTICATION(401, "ACC-AUTH-FAILED_TOKEN_AUTHENTICATION", "토큰 인증에 실패했습니다.."),
    INVALID_TOKEN(401, "ACC-AUTH-INVALID-TOKEN", "유효하지 않은 토큰입니다."),
    TOKEN_EXPIRED(401, "ACC-AUTH-TOKEN-EXPIRED", "토큰이 만료되었습니다."),
    SIGNATURE_DOES_NOT_MATCH(403, "ACC-AUTH-SIGNATURE-DOES-NOT-MATCH", "서명이 일치하지 않습니다."),
    NOT_FOUND_ACC_TOKEN(404, "ACC-AUTH-NOT-FOUND-TOKEN" , "ACC 토큰에 해당 하는 userId를 찾을 수 없습니다");
    private final int status;
    private final String code;
    private final String message;

    AuthErrorCode(int status, String code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }
}