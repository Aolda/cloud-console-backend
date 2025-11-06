package com.acc.global.exception.volume;

import com.acc.global.exception.ErrorCode;
import lombok.Getter;

@Getter
public enum VolumeErrorCode implements ErrorCode {
    // 400 Bad Request
    INVALID_REQUEST_PARAMETER(400, "ACC-STORAGE-SNAPSHOT-INVALID-PARAM", "필수 요청 파라미터가 누락되었거나 유효하지 않습니다."),
    INVALID_SNAPSHOT_ID(400, "ACC-STORAGE-SNAPSHOT-ID-INVALID", "스냅샷 ID 형식이 유효하지 않습니다 (UUID)."),
    INVALID_VOLUME_ID(400, "ACC-STORAGE-SNAPSHOT-VOLUME-ID-INVALID", "원본 볼륨 ID 형식이 유효하지 않습니다 (UUID)."),
    INVALID_SNAPSHOT_NAME(400, "ACC-STORAGE-SNAPSHOT-NAME-INVALID", "스냅샷 이름은 1~128자여야 하며, 영문/중문으로 시작하고 [0-9, a-z, A-Z, \"-_()[].:^\"] 문자만 포함할 수 있습니다."),
    // 403 Forbidden (권한 없음)
    FORBIDDEN_ACCESS(403, "ACC-STORAGE-SNAPSHOT-FORBIDDEN", "해당 프로젝트의 스냅샷에 접근할 권한이 없습니다."),

    // 404 Not Found (자원 없음)
    SNAPSHOT_NOT_FOUND(404, "ACC-STORAGE-SNAPSHOT-NOT-FOUND", "요청한 볼륨 스냅샷 ID를 찾을 수 없습니다."),
    MARKER_NOT_FOUND(404, "ACC-STORAGE-SNAPSHOT-MARKER-NOT-FOUND", "목록 조회의 기준(marker)이 되는 스냅샷 ID를 찾을 수 없습니다."),

    // 409 Conflict (충돌)
    SNAPSHOT_IN_USE(409, "ACC-STORAGE-SNAPSHOT-CONFLICT", "스냅샷이 사용 중이거나 삭제/생성이 불가능한 상태입니다."),

    // 500 Internal Server Error
    CINDER_API_FAILURE(500, "ACC-STORAGE-CINDER-API-FAILURE", "OpenStack Cinder API 통신 중 오류가 발생하였습니다.");

    private final int status;
    private final String code;
    private final String message;

    VolumeErrorCode(int status, String code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }
}
