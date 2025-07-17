package com.acc.global.exception.common;

import com.acc.global.exception.ErrorCode;
import lombok.Getter;

/**
 * 공통 에러코드
 */
@Getter
public enum CommonErrorCode implements ErrorCode {

    INTERNAL_FAILURE(500, "ACC-COMMON-INTERNAL-FAILURE", "서버 내부 오류입니다."),
    VALIDATION_ERROR(400, "ACC-COMMON-VALIDATION-ERROR", "입력값이 올바르지 않습니다."),
    THROTTLING(429, "ACC-COMMON-THROTTLING", "요청이 너무 많습니다."),
    SERVICE_UNAVAILABLE(503, "ACC-COMMON-SERVICE-UNAVAILABLE", "서비스를 사용할 수 없습니다.");

    private final int status;
    private final String code;
    private final String message;

    CommonErrorCode(int status, String code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }
}