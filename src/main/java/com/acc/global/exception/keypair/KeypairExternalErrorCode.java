package com.acc.global.exception.keypair;

import com.acc.global.exception.ErrorCode;
import lombok.Getter;

@Getter
public enum KeypairExternalErrorCode implements ErrorCode {

    KEYPAIR_EXTERNAL_INVALID_NAME(400, "ACC-KEYPAIR-EXTERNAL-INVALID-NAME", "키페어 이름이 유효하지 않습니다."),
    KEYPAIR_EXTERNAL_INVALID_REQUEST(400, "ACC-KEYPAIR-EXTERNAL-INVALID-REQUEST", "OpenStack API 요청이 잘못되었습니다. (Bad Request)"),

    KEYPAIR_EXTERNAL_UNAUTHORIZED(401, "ACC-KEYPAIR-EXTERNAL-UNAUTHORIZED", "OpenStack 인증에 실패했습니다. (Unauthorized)"),

    KEYPAIR_EXTERNAL_FORBIDDEN(403, "ACC-KEYPAIR-EXTERNAL-FORBIDDEN", "OpenStack API에 대한 권한이 없습니다. (Forbidden)"),

    KEYPAIR_EXTERNAL_NOT_FOUND(404, "ACC-KEYPAIR-EXTERNAL-NOT-FOUND", "OpenStack에서 해당 키페어를 찾을 수 없습니다."),

    KEYPAIR_EXTERNAL_ALREADY_EXISTS(409, "ACC-KEYPAIR-EXTERNAL-ALREADY-EXISTS", "이미 존재하는 키페어 이름입니다."),

    KEYPAIR_EXTERNAL_CREATION_FAILED(500, "ACC-KEYPAIR-EXTERNAL-CREATION-FAILED", "OpenStack 키페어 생성에 실패했습니다."),
    KEYPAIR_EXTERNAL_DELETION_FAILED(500, "ACC-KEYPAIR-EXTERNAL-DELETION-FAILED", "OpenStack 키페어 삭제에 실패했습니다.");

    private final int status;
    private final String code;
    private final String message;

    KeypairExternalErrorCode(int status, String code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }
}
