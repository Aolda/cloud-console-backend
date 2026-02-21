package com.acc.global.exception.auth;

import com.acc.global.exception.AccBaseException;
import com.acc.global.exception.ErrorCode;

/**
 * Keycloak External 레이어에서 사용하는 예외
 */
public class KeycloakException extends AccBaseException {

    public KeycloakException(ErrorCode errorCode) {
        super(errorCode);
    }

    public KeycloakException(ErrorCode errorCode, Throwable cause) {
        super(errorCode, errorCode.getMessage(), cause);
    }

    public KeycloakException(ErrorCode errorCode, String customMessage) {
        super(errorCode, customMessage);
    }

    public KeycloakException(ErrorCode errorCode, String customMessage, Throwable cause) {
        super(errorCode, customMessage, cause);
    }
}
