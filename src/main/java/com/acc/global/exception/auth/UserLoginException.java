package com.acc.global.exception.auth;

import com.acc.global.exception.AccBaseException;

public class UserLoginException extends AccBaseException {
    public UserLoginException( AuthErrorCode authErrorCode) {
        super(authErrorCode);
    }
}
