package com.acc.global.exception.notice;

import com.acc.global.exception.ErrorCode;
import lombok.Getter;

/**
 * Notice 에러코드
 */
@Getter
public enum NoticeErrorCode implements ErrorCode {

    // 404 Not Found
    NOTICE_NOT_FOUND(404, "ACC-NOTICE-NOTICE-NOT-FOUND", "요청한 공지를 찾을 수 없습니다.");

    private final int status;
    private final String code;
    private final String message;

    NoticeErrorCode(int status, String code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }
}