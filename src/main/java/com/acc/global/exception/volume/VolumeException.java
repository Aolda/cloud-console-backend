package com.acc.global.exception.volume;

import com.acc.global.exception.AccBaseException;
import com.acc.global.exception.ErrorCode;

public class VolumeException extends AccBaseException {
    public VolumeException(ErrorCode errorCode) {
        super(errorCode);
    }

    public VolumeException(ErrorCode errorCode, Throwable cause) {
        super(errorCode, errorCode.getMessage(), cause);
    }
}
