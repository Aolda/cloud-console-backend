package com.acc.global.exception;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
@Schema(description = "에러 응답 형식")
public class ErrorResponse {

    @Schema(description = "HTTP 상태 코드", example = "400")
    private final int status;

    @Schema(description = "도메인 에러 코드", example = "COMMON_INVALID_PARAMETER")
    private final String code;

    @Schema(description = "사람이 읽을 수 있는 에러 메시지", example = "요청 파라미터가 유효하지 않습니다.")
    private final String message;

    public ErrorResponse(ErrorCode errorCode) {
        this.status = errorCode.getStatus();
        this.code = errorCode.getCode();
        this.message = errorCode.getMessage();
    }

    public ErrorResponse(ErrorCode errorCode, String customMessage) {
        this.status = errorCode.getStatus();
        this.code = errorCode.getCode();
        this.message = customMessage;
    }
}
