package com.acc.global.exception.image;

import com.acc.global.exception.ErrorCode;
import lombok.Getter;

@Getter
public enum ImageErrorCode implements ErrorCode {
    IMAGE_FETCH_LIST_FAILURE(502, "ACC-IMAGE-FETCH-LIST-FAILURE", "이미지 목록 조회 중 오류가 발생했습니다."),
    IMAGE_DETAIL_FETCH_FAILURE(502, "ACC-IMAGE-DETAIL-FAILURE", "이미지 상세 조회 중 오류가 발생했습니다."),
    IMAGE_METADATA_CREATE_FAILURE(502, "ACC-IMAGE-METADATA-CREATE-FAILURE", "이미지 메타데이터 생성 중 오류가 발생했습니다."),
    IMAGE_IMPORT_FAILURE(502, "ACC-IMAGE-IMPORT-FAILURE", "이미지 import 중 오류가 발생하였습니다."),
    IMAGE_UPLOAD_FAILURE(500, "ACC-IMAGE-UPLOAD-FAILURE", "이미지 파일 업로드 중 오류가 발생하였습니다."),
    IMAGE_DELETE_FAILURE(502, "ACC-IMAGE-DELETE-FAILURE", "이미지 삭제 중 오류가 발생하였습니다."),
    INVALID_IMAGE_METADATA(400, "ACC-IMAGE-METADATA-INVALID", "이미지 메타데이터 형식이 올바르지 않습니다."),
    IMAGE_LIST_FETCH_FAILURE(502, "ACC-IMAGE-FETCH-LIST-FAILURE", "이미지 목록 조회 중 오류가 발생했습니다."),
    IMAGE_FILE_UPLOAD_FAILURE(502, "ACC-IMAGE-UPLOAD-FAILURE", "이미지 파일 업로드 중 오류가 발생했습니다."),
    INVALID_PAGINATION_PARAM(400, "ACC-IMAGE-PAGINATION-PARAM-INVALID", "잘못된 페이지네이션 파라미터 조합입니다."),
    INVALID_PAGINATION_WITH_IMAGE_ID(400, "ACC-IMAGE-PAGINATION-WITH-IMAGE-ID", "이미지 단건 조회 시 페이지네이션 파라미터를 함께 사용할 수 없습니다."),
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
