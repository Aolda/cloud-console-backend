package com.acc.global.exception.discord;

import com.acc.global.exception.ErrorCode;
import lombok.Getter;

@Getter
public enum DiscordErrorCode implements ErrorCode {

    MESSAGE_CREATION_FAILURE(500, "ACC-DISCORD-MESSAGE-FAILURE", "Discord 메시지 생성 중 오류가 발생했습니다."),
    WEBHOOK_SEND_FAILURE(500, "ACC-DISCORD-SEND-FAILURE", "Discord 웹훅 메시지 전송에 실패했습니다.");

    private final int status;
    private final String code;
    private final String message;

    DiscordErrorCode(int status, String code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }
}
