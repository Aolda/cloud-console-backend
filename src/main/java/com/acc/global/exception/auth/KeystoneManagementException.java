package com.acc.global.exception.auth;

import com.acc.global.exception.AccBaseException;


public class KeystoneManagementException extends AccBaseException {

    public KeystoneManagementException(AuthErrorCode authErrorCode , String customMessage, Throwable cause ) {
        super(authErrorCode, customMessage, cause);
    }

    public KeystoneManagementException(AuthErrorCode authErrorCode , String customMessage) {
        super(authErrorCode, customMessage);
    }

}
