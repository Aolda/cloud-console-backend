package com.acc.global.exception.common;

import com.acc.global.exception.AccBaseException;
import com.acc.global.exception.ErrorCode;

/**
 * ===== Exception 예시 ========
 */
public class ServiceUnavailableException extends AccBaseException {

    /**
     * 기본 에러 메시지로 예외 생성
     */
    public ServiceUnavailableException(ErrorCode errorCode, String customMessage) {super(errorCode,  customMessage);}

    /**
     * 커스텀 메시지로 예외 생성(커스텀 형식은 자유)
     */
    public ServiceUnavailableException(String customMessage) {
        super(CommonErrorCode.SERVICE_UNAVAILABLE, customMessage + "와 같은 이유로 이용할 수 없습니다.");
    }

    /**
     * 커스텀 메시지 + Exception Chaining 으로 예외 생성
     */
    public ServiceUnavailableException(String customMessage, Throwable cause) {
        super(CommonErrorCode.SERVICE_UNAVAILABLE, customMessage, cause);
    }
}
