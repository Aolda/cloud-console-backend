package com.acc.global.exception.auth;

import com.acc.global.exception.ErrorCode;
import lombok.Getter;

/**
 * Auth 에러코드
 */
@Getter
public enum AuthErrorCode implements ErrorCode {

    // 400 Bad Request
    INVALID_REQUEST_PARAMETER(400, "ACC-AUTH-INVALID-REQUEST-PARAM", "필수 요청 파라미터가 누락되었거나 유효하지 않습니다."),
    INVALID_PROJECT_STATUS(400, "ACC-AUTH-INVALID-PROJECT-STATUS", "잘못된 프로젝트 상태 값입니다."),

    // 401 Unauthorized
    UNAUTHORIZED(401, "ACC-AUTH-UNAUTHORIZED", "인증에 실패했습니다."),
    KEYSTONE_TOKEN_GENERATION_FAILED(401, "ACC-AUTH-KEYSTONE-TOKEN-GENERATION-FAILED", "Keystone 토큰 생성에 실패했습니다."),
    INVALID_TOKEN(401, "ACC-AUTH-INVALID-TOKEN", "유효하지 않은 토큰입니다."),
    TOKEN_EXPIRED(401, "ACC-AUTH-TOKEN-EXPIRED", "토큰이 만료되었습니다."),

    // 403 Forbidden
    FORBIDDEN_ACCESS(403, "ACC-AUTH-FORBIDDEN", "해당 리소스에 접근할 권한이 없습니다."),
    // SIGNATURE_DOES_NOT_MATCH(403, "ACC-AUTH-SIGNATURE-DOES-NOT-MATCH", "서명이 일치하지 않습니다."),
    KEYSTONE_TOKEN_EXPIRED(403, "ACC-AUTH-KEYSTONE-TOKEN-EXPIRED", "Keystone 인증이 만료되었습니다."),
    KEYSTONE_TOKEN_AUTHENTICATION_FAILED(403, "ACC-AUTH-KEYSTONE-AUTHENTICATION-FAILED", "Keystone 토큰 인증에 실패했습니다."),
    AJOU_STUDENT_VERIFICATION_FAILED(403, "ACC-AUTH-AJOU-STUDENT-VERIFICATION-FAILED", "아주대 학생 인증에 실패했습니다."),

    // 404 Not Found
    NOT_FOUND_ACC_TOKEN(404, "ACC-AUTH-NOT-FOUND-TOKEN", "ACC 토큰에 해당 하는 userId를 찾을 수 없습니다."),
    USER_NOT_FOUND(404, "ACC-AUTH-USER-NOT-FOUND", "요청한 사용자를 찾을 수 없습니다."),
    PROJECT_NOT_FOUND(404, "ACC-AUTH-PROJECT-NOT-FOUND", "요청한 프로젝트를 찾을 수 없습니다."),
    // NOT_FOUND_USER(404 , "ACC-AUTH-NOT-FOUND-USER" , "ACC 유저 계정을 찾을 수 없습니다."),

    // 409 Conflict
    CONFLICT(409, "ACC-AUTH-CONFLICT", "리소스가 충돌되었습니다. 이미 존재하거나 삭제할 수 없는 상태입니다."),
    OAUTH2_USER_ALREADY_EXISTS(409, "ACC-AUTH-OAUTH2-USER-ALREADY-EXISTS", "이미 존재하는 계정입니다."),

    // 500 Internal Server Error
    KEYSTONE_API_FAILURE(500, "ACC-AUTH-KEYSTONE-API-FAILURE", "OpenStack Keystone API 통신 중 오류가 발생하였습니다."),
    KEYSTONE_USER_CREATION_FAILED(500, "ACC-AUTH-KEYSTONE-CREATION-FAILED", "Keystone 사용자 생성에 실패했습니다."),
    KEYSTONE_PROJECT_CREATION_FAILED(500, "ACC-AUTH-KEYSTONE-PROJECT-CREATION-FAILED", "Keystone 프로젝트 생성에 실패했습니다."),
    KEYSTONE_PROJECT_RETRIEVAL_FAILED(500, "ACC-AUTH-KEYSTONE-PROJECT-RETRIEVAL-FAILED", "Keystone 프로젝트 조회에 실패했습니다."),
    KEYSTONE_PROJECT_UPDATE_FAILED(500, "ACC-AUTH-KEYSTONE-PROJECT-UPDATE-FAILED", "Keystone 프로젝트 업데이트에 실패했습니다."),
    KEYSTONE_PROJECT_DELETION_FAILED(500, "ACC-AUTH-KEYSTONE-PROJECT-DELETION-FAILED", "Keystone 프로젝트 삭제에 실패했습니다."),
    KEYSTONE_TOKEN_EXTRACTION_FAILED(500, "ACC-AUTH-KEYSTONE-TOKEN-EXTRACTION-FAILED", "Keystone 토큰 추출에 실패했습니다."),
    KEYSTONE_RESPONSE_PARSING_FAILED(500, "ACC-AUTH-KEYSTONE-RESPONSE-PARSING-FAILED", "Keystone 응답 파싱에 실패했습니다."),
    KEYSTONE_INVALID_RESPONSE_STRUCTURE(500, "ACC-AUTH-KEYSTONE-INVALID-RESPONSE-STRUCTURE", "Keystone 응답 구조가 올바르지 않습니다."),
    SIGN_UP_ERROR(500,"ACC-AUTH-SIGNUP-ERROR" ,"회원가입 도중 에러가 발생했습니다." );
    private final int status;
    private final String code;
    private final String message;

    AuthErrorCode(int status, String code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }
}
