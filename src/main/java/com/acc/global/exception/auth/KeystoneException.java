package com.acc.global.exception.auth;

import com.acc.global.exception.AccBaseException;
import com.acc.global.exception.ErrorCode;

/**
 * Keystone External 레이어에서 사용하는 예외
 */
public class KeystoneException extends AccBaseException {

    public KeystoneException(ErrorCode errorCode) {
        super(errorCode);
    }

    public KeystoneException(ErrorCode errorCode, Throwable cause) {
        super(errorCode, errorCode.getMessage(), cause);
    }

    public KeystoneException(ErrorCode errorCode, String customMessage) {
        super(errorCode, customMessage);
    }

    public KeystoneException(ErrorCode errorCode, String customMessage, Throwable cause) {
        super(errorCode, customMessage, cause);
    }
}