package com.acc.global.exception.image;

import com.acc.global.exception.AccBaseException;
import com.acc.global.exception.ErrorCode;

public class ImageException extends AccBaseException {

    public ImageException(ErrorCode errorCode) {
        super(errorCode);
    }

    public ImageException(ErrorCode errorCode, Throwable cause) {
        super(errorCode, errorCode.getMessage(), cause);
    }
}
