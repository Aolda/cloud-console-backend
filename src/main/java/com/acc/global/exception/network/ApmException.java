package com.acc.global.exception.network;

import com.acc.global.exception.AccBaseException;

public class ApmException extends AccBaseException {
    public ApmException(ApmErrorCode errorCode) {
        super(errorCode);
    }

    public ApmException(ApmErrorCode errorCode, Throwable cause) {
        super(errorCode, errorCode.getMessage(), cause);
    }
}
