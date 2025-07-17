package com.acc.global.exception;

import com.acc.global.exception.common.ServiceUnavailableException;
import lombok.Getter;

/**
 * 모든 도메인 예외들의 공통 부모 역할
 * 사용 예시:
 * @see ServiceUnavailableException
 */
@Getter
public abstract class AccBaseException extends RuntimeException {


    private final ErrorCode errorCode;      // 필수: 에러 코드 정보

    private final String customMessage;     // 선택: 커스텀 메세지

    /**
     * 기본 ErrorCode 형식 사용하여 예외 생성
     */
    public AccBaseException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
        this.customMessage = errorCode.getMessage();
    }

    /**
     * ErrorCode + 커스텀 메시지로 예외 생성
     */
    public AccBaseException(ErrorCode errorCode, String customMessage) {
        super(customMessage);
        this.errorCode = errorCode;
        this.customMessage = customMessage;
    }

    /**
     * ErrorCode + 커스텀 메시지 + Exception chaining 으로 예외 생성
     */
    public AccBaseException(ErrorCode errorCode, String customMessage, Throwable cause) {
        super(customMessage, cause);
        this.errorCode = errorCode;
        this.customMessage = customMessage;
    }
}
