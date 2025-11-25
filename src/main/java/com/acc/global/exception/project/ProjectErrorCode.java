package com.acc.global.exception.project;

import com.acc.global.exception.ErrorCode;

import lombok.Getter;

/**
 * Auth 에러코드
 */
@Getter
public enum ProjectErrorCode implements ErrorCode {

    // 400 Bad Request
    INVALID_REQUEST_PARAMETER(400, "ACC-PROJECT-INVALID-REQUEST-PARAM", "필수 요청 파라미터가 누락되었거나 유효하지 않습니다."),
    INVALID_PROJECT_STATUS(400, "ACC-PROJECT-INVALID-PROJECT-STATUS", "잘못된 프로젝트 상태 값입니다."),
    INVALID_PROJECT_REQUEST_TYPE(400, "ACC-PROJECT-INVALID-PROJECT-REQUEST-TYPE", "올바르지 않은 프로젝트 요청타입 입니다."),
    INVALID_PROJECT_REQUEST_STATUS(400, "ACC-PROJECT-INVALID-PROJECT-REQUEST-STATUS", "올바르지 않은 프로젝트 요청상태 입니다."),

    // 403 Forbidden
    FORBIDDEN_ACCESS(403, "ACC-PROJECT-FORBIDDEN", "해당 리소스에 접근할 권한이 없습니다."),

    // 404 Not Found
    PROJECT_NOT_FOUND(404, "ACC-PROJECT-PROJECT-NOT-FOUND", "요청한 프로젝트를 찾을 수 없습니다."),
    USER_NOT_FOUND(404, "ACC-AUTH-USER-NOT-FOUND", "요청한 사용자를 찾을 수 없습니다."),

    // 409 Conflict
    CONFLICT(409, "ACC-PROJECT-CONFLICT", "리소스가 충돌되었습니다. 이미 존재하거나 삭제할 수 없는 상태입니다."),

    // 500 Internal Server Error
    UNEXPECTED_ERROR(500,"ACC-PROJECT-UNEXPECTED-ERROR" ,"예상치 못한 에러가 발생하였습니다. 잠시 후 다시 시도해주세요." );

    private final int status;
    private final String code;
    private final String message;

    ProjectErrorCode(int status, String code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }
}
