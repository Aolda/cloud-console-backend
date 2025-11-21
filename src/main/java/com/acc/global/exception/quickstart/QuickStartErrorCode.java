package com.acc.global.exception.quickstart;

import com.acc.global.exception.ErrorCode;
import lombok.Getter;

@Getter
public enum QuickStartErrorCode implements ErrorCode {

    QUICK_START_INTERFACE_CREATE_FAILED(500, "ACC-QUICK-START-INTERFACE-CREATION-FAILED", "빠른 생성 인터페이스 생성에 실패했습니다."),
    QUICK_START_FORWARDING_CREATE_FAILED(500, "ACC-NETWORK-APM-FORWARDING-CREATION-FAILED", "빠른 생성 포트포워딩 생성에 실패했습니다.");

    private final int status;
    private final String code;
    private final String message;

    QuickStartErrorCode(int status, String code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }
}
