package com.acc.global.exception.image;

import com.acc.global.exception.ErrorCode;
import lombok.Getter;

@Getter
public enum ImageErrorCode implements ErrorCode {

    INVALID_IMAGE_ID(400, "ACC-IMAGE-ID-INVALID", "잘못된 이미지 ID 형식입니다."),
    INVALID_PAGINATION_PARAM(400, "ACC-IMAGE-PAGINATION-PARAM-INVALID", "잘못된 페이지네이션 파라미터 조합입니다."),
    INVALID_PAGINATION_WITH_IMAGE_ID(400, "ACC-IMAGE-PAGINATION-WITH-IMAGE-ID", "단건 조회 시 페이지네이션 파라미터를 사용할 수 없습니다."),
    INVALID_IMPORT_URL(400, "ACC-IMAGE-IMPORT-URL-INVALID", "이미지 URL 형식이 올바르지 않습니다."),
    INVALID_IMAGE_METADATA(400, "ACC-IMAGE-METADATA-INVALID", "이미지 메타데이터 형식이 올바르지 않습니다."),

    IMAGE_NOT_FOUND(404, "ACC-IMAGE-NOT-FOUND", "요청한 이미지를 찾을 수 없습니다."),
    IMAGE_NOT_ACCESSIBLE(403, "ACC-IMAGE-NO-PERMISSION", "해당 이미지에 접근할 권한이 없습니다."),
    IMAGE_ALREADY_EXISTS(409, "ACC-IMAGE-ALREADY-EXISTS", "이미 동일한 이미지가 존재합니다."),
    IMAGE_STATUS_CONFLICT(502, "ACC-IMAGE-STATUS-CONFLICT", "현재 상태에서는 요청을 처리할 수 없습니다."),
    IMAGE_DELETE_FORBIDDEN(403, "ACC-IMAGE-DELETE-FORBIDDEN", "이미지를 삭제할 권한이 없습니다."),

    GLANCE_UNAVAILABLE(502, "ACC-GLANCE-UNAVAILABLE", "Glance 서비스와 통신할 수 없습니다."),
    GLANCE_BAD_RESPONSE(502, "ACC-GLANCE-BAD-RESPONSE", "Glance로부터 비정상 응답이 수신되었습니다."),

    IMAGE_IMPORT_FAILURE(502, "ACC-IMAGE-IMPORT-FAILURE", "이미지 import 중 오류가 발생하였습니다."),
    IMAGE_METADATA_CREATE_FORBIDDEN(403, "ACC-IMAGE-METADATA-CREATE-FORBIDDEN", "이미지 메타데이터를 생성할 권한이 없습니다."),
    IMAGE_UPLOAD_FAILURE(500, "ACC-IMAGE-UPLOAD-FAILURE", "이미지 파일 업로드 중 오류가 발생하였습니다."),
    IMAGE_DELETE_FAILURE(502, "ACC-IMAGE-DELETE-FAILURE", "이미지 삭제 중 오류가 발생하였습니다."),

    INVALID_QUICK_START_IMAGE(502, "ACC-FETCH-DEFAULT-IMAGE-FAIL", "빠른 생성 이미지 조회 및 유효성 검사에 실패했습니다.");



    private final int status;
    private final String code;
    private final String message;

    ImageErrorCode(int status, String code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }
}

