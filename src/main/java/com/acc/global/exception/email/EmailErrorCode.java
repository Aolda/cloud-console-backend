package com.acc.global.exception.email;

import com.acc.global.exception.ErrorCode;
import lombok.Getter;

@Getter
public enum EmailErrorCode implements ErrorCode {

    EMAIL_TEMPLATE_PROCESSING_FAILURE(500, "ACC-EMAIL-TEMPLATE-FAILURE", "이메일 템플릿 처리 중 오류가 발생했습니다."),
    EMAIL_MESSAGE_CREATION_FAILURE(500, "ACC-EMAIL-MESSAGE-FAILURE", "이메일 메시지 생성 중 오류가 발생했습니다."),
    EMAIL_SEND_FAILURE(500, "ACC-EMAIL-SEND-FAILURE", "이메일 전송에 실패했습니다.");

    private final int status;
    private final String code;
    private final String message;

    EmailErrorCode(int status, String code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }
}
