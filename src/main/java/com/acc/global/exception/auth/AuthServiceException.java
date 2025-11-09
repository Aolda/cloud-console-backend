package com.acc.global.exception.auth;

import com.acc.global.exception.AccBaseException;
import com.acc.global.exception.ErrorCode;

/**
 * Auth Service 레이어에서 사용하는 예외
 */
public class AuthServiceException extends AccBaseException {

    public AuthServiceException(ErrorCode errorCode) {
        super(errorCode);
    }

    public AuthServiceException(ErrorCode errorCode, Throwable cause) {
        super(errorCode, errorCode.getMessage(), cause);
    }

    public AuthServiceException(ErrorCode errorCode, String customMessage) {
        super(errorCode, customMessage);
    }

    public AuthServiceException(ErrorCode errorCode, String customMessage, Throwable cause) {
        super(errorCode, customMessage, cause);
    }
}