package com.acc.global.exception.auth;

import com.acc.global.exception.AccBaseException;
import com.acc.global.exception.ErrorCode;


public class JwtAuthenticationException extends AccBaseException {

    public JwtAuthenticationException() {
        super(AuthErrorCode.FAILED_TOKEN_AUTHENTICATION);
    }
}
