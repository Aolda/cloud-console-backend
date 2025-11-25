package com.acc.global.exception.type;

import com.acc.global.exception.AccBaseException;
import com.acc.global.exception.ErrorCode;

public class FlavorExternalException extends AccBaseException {

    public FlavorExternalException(ErrorCode errorCode) {
        super(errorCode);
    }

    public FlavorExternalException(ErrorCode errorCode, String customMessage) {
        super(errorCode, customMessage);
    }
}
