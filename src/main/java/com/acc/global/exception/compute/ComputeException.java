package com.acc.global.exception.compute;

import com.acc.global.exception.AccBaseException;
import com.acc.global.exception.ErrorCode;

public class ComputeException extends AccBaseException {
    public ComputeException(ErrorCode errorCode) {
        super(errorCode);
    }

    public ComputeException(ErrorCode errorCode, Throwable cause) {
        super(errorCode, errorCode.getMessage(), cause);
    }
}
