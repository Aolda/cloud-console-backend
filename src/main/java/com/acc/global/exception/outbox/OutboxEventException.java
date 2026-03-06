package com.acc.global.exception.outbox;

import com.acc.global.exception.AccBaseException;

public class OutboxEventException extends AccBaseException {

    private final boolean unrecoverable;

    public OutboxEventException(OutboxErrorCode errorCode, String customMessage) {
        super(errorCode, customMessage);
        this.unrecoverable = errorCode.getStatus() != 500;
    }

    public OutboxEventException(OutboxErrorCode errorCode, String customMessage, Throwable cause) {
        super(errorCode, customMessage, cause);
        this.unrecoverable = errorCode.getStatus() != 500;
    }

    /**
     * 재시도해도 의미 없는 복구 불가 예외 여부
     * true 이면 즉시 FAILED 처리하고 재시도를 중단
     */
    public boolean isUnrecoverable() {
        return unrecoverable;
    }
}






