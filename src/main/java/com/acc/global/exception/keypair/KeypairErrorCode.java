package com.acc.global.exception.keypair;

import com.acc.global.exception.ErrorCode;
import lombok.Getter;

@Getter
public enum KeypairErrorCode implements ErrorCode {

    INVALID_KEYPAIR_NAME(400, "ACC-KEYPAIR-INVALID-NAME", "키페어 이름이 유효하지 않습니다."),

    DB_PROJECT_NOT_FOUND(404, "ACC-KEYPAIR-DB-PROJECT-NOT-FOUND", "프로젝트를 찾을 수 없습니다. (DB)"),
    DB_KEYPAIR_NOT_FOUND(404, "ACC-KEYPAIR-DB-NOT-FOUND", "키페어를 찾을 수 없습니다. (DB)"),

    DB_SAVE_FAILED(500, "ACC-KEYPAIR-DB-SAVE-FAILED", "키페어 정보를 DB에 저장하는 데 실패했습니다."),
    DB_DELETION_FAILED(500, "ACC-KEYPAIR-DB-DELETION-FAILED", "키페어 정보를 DB에 저장하는 데 실패했습니다.");


    private final int status;
    private final String code;
    private final String message;

    KeypairErrorCode(int status, String code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }
}
