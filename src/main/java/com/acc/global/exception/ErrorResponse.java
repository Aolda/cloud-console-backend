package com.acc.global.exception;

import lombok.Getter;

/**
 * API 에러 응답용 DTO
 */
@Getter
public class ErrorResponse {

    private final int status;

    private final String code;

    private final String message;

    /**
     * ErrorCode의 기본 메시지로 에러 응답 생성
     */
    public ErrorResponse(ErrorCode errorCode) {
        this.status = errorCode.getStatus();
        this.code = errorCode.getCode();
        this.message = errorCode.getMessage();
    }

    /**
     * ErrorCode와 커스텀 메시지로 에러 응답 생성
     */
    public ErrorResponse(ErrorCode errorCode, String customMessage) {
        this.status = errorCode.getStatus();
        this.code = errorCode.getCode();
        this.message = customMessage;
    }
}