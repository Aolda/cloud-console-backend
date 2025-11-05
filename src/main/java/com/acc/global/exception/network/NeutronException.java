package com.acc.global.exception.network;

import com.acc.global.exception.AccBaseException;

public class NeutronException extends AccBaseException {
    public NeutronException(NeutronErrorCode errorCode) {
        super(errorCode);
    }

    public NeutronException(NeutronErrorCode errorCode, Throwable cause) {
        super(errorCode, errorCode.getMessage(), cause);
    }
}
