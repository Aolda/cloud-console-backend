package com.acc.global.exception.auth;

import com.acc.global.exception.AccBaseException;

public class AuthEntityException extends AccBaseException {

    public AuthEntityException(AuthErrorCode authErrorCode , String customMessage, Throwable cause ) {
        super(authErrorCode, customMessage, cause);
    }

    public AuthEntityException(AuthErrorCode authErrorCode , String customMessage) {
        super(authErrorCode, customMessage);
    }

    public AuthEntityException(AuthErrorCode authErrorCode) {
        super(authErrorCode, authErrorCode.getMessage());
    }

}
