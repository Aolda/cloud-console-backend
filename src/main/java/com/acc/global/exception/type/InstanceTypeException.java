package com.acc.global.exception.type;

import com.acc.global.exception.AccBaseException;
import com.acc.global.exception.ErrorCode;

public class InstanceTypeException extends AccBaseException {

    public InstanceTypeException(ErrorCode errorCode) {
        super(errorCode);
    }

    public InstanceTypeException(ErrorCode errorCode, String customMessage) {
        super(errorCode, customMessage);
    }
}
