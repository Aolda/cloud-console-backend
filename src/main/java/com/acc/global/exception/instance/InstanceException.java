package com.acc.global.exception.instance;

import com.acc.global.exception.AccBaseException;
import com.acc.global.exception.ErrorCode;

public class InstanceException extends AccBaseException {

    public InstanceException(ErrorCode errorCode) {
        super(errorCode);
    }

    public InstanceException(ErrorCode errorCode, String customMessage) {
        super(errorCode, customMessage);
    }
}
