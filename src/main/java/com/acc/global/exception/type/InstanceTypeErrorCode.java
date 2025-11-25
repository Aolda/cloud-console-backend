package com.acc.global.exception.type;

import com.acc.global.exception.ErrorCode;
import lombok.Getter;

@Getter
public enum InstanceTypeErrorCode implements ErrorCode {

    INVALID_INSTANCE_TYPE_NAME(400, "ACC-INSTANCE-TYPE-INVALID-NAME", "인스턴스 타입 이름이 유효하지 않습니다.");

    private final int status;
    private final String code;
    private final String message;

    InstanceTypeErrorCode(int status, String code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }
}
