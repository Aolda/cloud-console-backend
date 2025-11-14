package com.acc.global.exception.network;

import com.acc.global.exception.ErrorCode;
import lombok.Getter;

@Getter
public enum ApmErrorCode implements ErrorCode {

    APM_FORWARDING_CREATION_FAILED(500, "ACC-NETWORK-APM-FORWARDING-CREATION-FAILED", "APM 포트포워딩 생성에 실패했습니다."),
    APM_FORWARDING_DELETION_FAILED(500, "ACC-NETWORK-APM-FORWARDING-DELETION-FAILED", "APM 포트포워딩 삭제에 실패했습니다."),
    APM_FORWARDING_RETRIEVAL_FAILED(500, "ACC-NETWORK-APM-FORWARDING-RETRIEVAL-FAILED", "APM 포트포워딩 조회에 실패했습니다.");;

    private final int status;
    private final String code;
    private final String message;

    ApmErrorCode(int status, String code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }
}
