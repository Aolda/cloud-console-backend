package com.acc.global.exception.type;

import com.acc.global.exception.ErrorCode;
import lombok.Getter;

@Getter
public enum FlavorExternalErrorCode implements ErrorCode {

    FLAVOR_EXTERNAL_INVALID_REQUEST(400, "ACC-FLAVOR-EXTERNAL-INVALID-REQUEST", "OpenStack API 요청이 잘못되었습니다. (Bad Request)"),
    FLAVOR_EXTERNAL_UNAUTHORIZED(401, "ACC-FLAVOR-EXTERNAL-UNAUTHORIZED", "OpenStack 인증에 실패했습니다. (Unauthorized)"),
    FLAVOR_EXTERNAL_FORBIDDEN(403, "ACC-FLAVOR-EXTERNAL-FORBIDDEN", "OpenStack API에 대한 권한이 없습니다. (Forbidden)"),
    FLAVOR_EXTERNAL_ALREADY_EXISTS(409, "ACC-FLAVOR-EXTERNAL-ALREADY-EXISTS", "이미 존재하는 Flavor 이름입니다."),
    FLAVOR_EXTERNAL_CREATION_FAILED(500, "ACC-FLAVOR-EXTERNAL-CREATION-FAILED", "Flavor 생성하는데 실패했습니다."),
    FLAVOR_EXTERNAL_RETRIEVAL_FAILED(500, "ACC-FLAVOR-EXTERNAL-RETRIEVAL-FAILED", "Flavor 정보를 가져오는데 실패했습니다.");

    private final int status;
    private final String code;
    private final String message;

    FlavorExternalErrorCode(int status, String code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }
}
