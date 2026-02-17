package com.acc.global.exception.session;

import com.acc.global.exception.ErrorCode;
import lombok.Getter;

@Getter
public enum SessionErrorCode implements ErrorCode {

    // 404 Not Found
    SESSION_NOT_FOUND(404, "ACC-SESSION-NOT-FOUND", "세션을 찾을 수 없습니다."),
    SESSION_FIELD_NOT_FOUND(404, "ACC-SESSION-FIELD-NOT-FOUND", "세션 필드를 찾을 수 없습니다."),

    // 500 Internal Server Error
    SESSION_SAVE_FAILED(500, "ACC-SESSION-SAVE-FAILED", "세션 저장에 실패했습니다."),
    SESSION_READ_FAILED(500, "ACC-SESSION-READ-FAILED", "세션 조회에 실패했습니다."),
    SESSION_DELETE_FAILED(500, "ACC-SESSION-DELETE-FAILED", "세션 삭제에 실패했습니다."),
    SESSION_REDIS_CONNECTION_FAILED(500, "ACC-SESSION-REDIS-CONNECTION-FAILED", "Redis 연결에 실패했습니다.");

    private final int status;
    private final String code;
    private final String message;

    SessionErrorCode(int status, String code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }
}
