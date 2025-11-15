package com.acc.global.exception.instance;

import com.acc.global.exception.AccBaseException;
import com.acc.global.exception.ErrorCode;

public class NovaException extends AccBaseException {

    public NovaException(ErrorCode errorCode) {
        super(errorCode);
    }

    public NovaException(ErrorCode errorCode, String customMessage) {
        super(errorCode, customMessage);
    }
}
