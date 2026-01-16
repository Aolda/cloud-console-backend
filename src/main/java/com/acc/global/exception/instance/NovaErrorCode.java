package com.acc.global.exception.instance;

import com.acc.global.exception.ErrorCode;
import lombok.Getter;

@Getter
public enum NovaErrorCode implements ErrorCode {

    // instance
    NOVA_UNSUPPORTED_ACTION(400, "ACC-NOVA-UNSUPPORTED-ACTION", "지원되지 않는 작업(Action) 유형입니다."),
    NOVA_SERVER_NOT_FOUND(404, "ACC-NOVA-SERVER-NOT-FOUND", "Nova에서 해당 서버를 찾을 수 없습니다."),
    NOVA_SERVER_RETRIEVAL_FAILED(500, "ACC-NOVA-RETRIEVAL-FAILED", "Nova 서버 정보를 가져오는데 실패했습니다."),
    NOVA_SERVER_CREATION_FAILED(500, "ACC-NOVA-CREATION-FAILED", "Nova 서버 생성하는데 실패했습니다."),

    // instance action
    NOVA_SERVER_ACTION_FAILED(500, "ACC-NOVA-ACTION-FAILED", "Nova 서버 Action 수행에 실패했습니다."),

    // instance interface
    NOVA_SERVER_INTERFACE_CREATION_FAILED(500, "ACC-NOVA-INTERFACE-CREATION-FAILED", "Nova 서버 인터페이스 연결에 실패했습니다."),
    NOVA_SERVER_INTERFACE_RETRIEVAL_FAILED(500, "ACC-NOVA-INTERFACE-RETRIEVAL-FAILED", "Nova 서버 인터페이스 조회에 실패했습니다."),
    NOVA_SERVER_INTERFACE_DELETION_FAILED(500, "ACC-NOVA-INTERFACE-DELETION-FAILED", "Nova 서버 인터페이스 분리에 실패했습니다.");

    private final int status;
    private final String code;
    private final String message;

    NovaErrorCode(int status, String code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }
}
