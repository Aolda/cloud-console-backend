package com.acc.global.exception.project;

import com.acc.global.exception.AccBaseException;
import com.acc.global.exception.ErrorCode;

/**
 * Project Service 레이어에서 사용하는 예외
 */
public class ProjectServiceException extends AccBaseException {

    public ProjectServiceException(ErrorCode errorCode) {
        super(errorCode);
    }

    public ProjectServiceException(ErrorCode errorCode, Throwable cause) {
        super(errorCode, errorCode.getMessage(), cause);
    }

    public ProjectServiceException(ErrorCode errorCode, String customMessage) {
        super(errorCode, customMessage);
    }

    public ProjectServiceException(ErrorCode errorCode, String customMessage, Throwable cause) {
        super(errorCode, customMessage, cause);
    }
}
