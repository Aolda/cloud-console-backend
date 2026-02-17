package com.acc.global.exception.session;

import com.acc.global.exception.AccBaseException;
import com.acc.global.exception.ErrorCode;

public class SessionException extends AccBaseException {

    public SessionException(ErrorCode errorCode) {
        super(errorCode);
    }

    public SessionException(ErrorCode errorCode, Throwable cause) {
        super(errorCode, errorCode.getMessage(), cause);
    }

    public SessionException(ErrorCode errorCode, String customMessage) {
        super(errorCode, customMessage);
    }

    public SessionException(ErrorCode errorCode, String customMessage, Throwable cause) {
        super(errorCode, customMessage, cause);
    }
}
