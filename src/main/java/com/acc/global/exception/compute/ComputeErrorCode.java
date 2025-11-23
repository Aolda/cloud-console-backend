package com.acc.global.exception.compute;

import com.acc.global.exception.ErrorCode;

import lombok.Getter;

@Getter
public enum ComputeErrorCode implements ErrorCode {
    // 400 Bad Request
    INVALID_REQUEST_PARAMETER(400, "ACC-COMPUTE-INVALID-PARAM", "필수 요청 파라미터가 누락되었거나 유효하지 않습니다."),

    // 403 Forbidden (권한 없음)
    FORBIDDEN_ACCESS(403, "ACC-COMPUTE-FORBIDDEN", "본 API에 접근할 권한이 없습니다"),

    // 404 Not Found (자원 없음)
    COMPUTE_NOT_FOUND(404, "ACC-COMPUTE-NOT-FOUND", "COMPUTE 404 Not Found"),

    // 500 Internal Server Error
    NOVA_API_FAILURE(500, "ACC-COMPUTE-NOVA-API-FAILURE", "OpenStack Nova API 통신 중 오류가 발생하였습니다.");

    private final int status;
    private final String code;
    private final String message;

    ComputeErrorCode(int status, String code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }
}
