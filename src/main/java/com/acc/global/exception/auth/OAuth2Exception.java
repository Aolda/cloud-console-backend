package com.acc.global.exception.auth;

import com.acc.global.exception.AccBaseException;

public class OAuth2Exception extends AccBaseException {


    public OAuth2Exception(AuthErrorCode errorCode) {
        super(errorCode);
    }

    public OAuth2Exception(AuthErrorCode code , String message) {
        super(code, message);
    }

    public OAuth2Exception(AuthErrorCode code , String message, Throwable cause) {
        super(code, message, cause);
    }
}