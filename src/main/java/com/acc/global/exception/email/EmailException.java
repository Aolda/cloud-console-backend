package com.acc.global.exception.email;

import com.acc.global.exception.AccBaseException;
import com.acc.global.exception.ErrorCode;

public class EmailException extends AccBaseException {

    public EmailException(ErrorCode errorCode) {
        super(errorCode);
    }

    public EmailException(ErrorCode errorCode, Throwable cause) {
        super(errorCode, errorCode.getMessage(), cause);
    }
}
