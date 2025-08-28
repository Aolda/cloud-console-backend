package com.acc.global.exception.discord;

import com.acc.global.exception.AccBaseException;
import com.acc.global.exception.ErrorCode;

public class DiscordException extends AccBaseException {

    public DiscordException(ErrorCode errorCode) {
        super(errorCode);
    }

    public DiscordException(ErrorCode errorCode, Throwable cause) {
        super(errorCode, errorCode.getMessage(), cause);
    }
}
