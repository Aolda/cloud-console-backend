package com.acc.global.exception.notice;

import com.acc.global.exception.AccBaseException;
import com.acc.global.exception.ErrorCode;

/**
 * Notice Service 레이어에서 사용하는 예외
 */
public class NoticeServiceException extends AccBaseException {

    public NoticeServiceException(ErrorCode errorCode) {
        super(errorCode);
    }

    public NoticeServiceException(ErrorCode errorCode, Throwable cause) {
        super(errorCode, errorCode.getMessage(), cause);
    }

    public NoticeServiceException(ErrorCode errorCode, String customMessage) {
        super(errorCode, customMessage);
    }

    public NoticeServiceException(ErrorCode errorCode, String customMessage, Throwable cause) {
        super(errorCode, customMessage, cause);
    }
}