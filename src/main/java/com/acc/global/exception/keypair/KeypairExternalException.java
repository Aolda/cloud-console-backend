package com.acc.global.exception.keypair;

import com.acc.global.exception.AccBaseException;
import com.acc.global.exception.ErrorCode;

public class KeypairExternalException extends AccBaseException {
    public KeypairExternalException(ErrorCode errorCode) {
        super(errorCode);
    }
}
