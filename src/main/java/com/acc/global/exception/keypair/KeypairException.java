package com.acc.global.exception.keypair;

import com.acc.global.exception.AccBaseException;
import com.acc.global.exception.ErrorCode;

public class KeypairException extends AccBaseException {
    public KeypairException(ErrorCode errorCode) {
        super(errorCode);
    }
}
