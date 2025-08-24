package com.acc.global.exception.auth;

import com.acc.global.exception.AccBaseException;


public class JwtAuthenticationException extends AccBaseException {

    public JwtAuthenticationException(AuthErrorCode authErrorCode) {
        super(authErrorCode);
    }
}
