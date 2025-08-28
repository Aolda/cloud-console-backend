package com.acc.global.exception.google;

import com.acc.global.exception.AccBaseException;
import com.acc.global.exception.ErrorCode;

public class GoogleException extends AccBaseException {

    public GoogleException(ErrorCode errorCode) {
        super(errorCode);
    }

    public GoogleException(ErrorCode errorCode, Throwable cause) {
        super(errorCode, errorCode.getMessage(), cause);
    }
}
