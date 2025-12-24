package com.acc.global.exception.volume;

import com.acc.global.exception.ErrorCode;
import lombok.Getter;

@Getter
public enum VolumeErrorCode implements ErrorCode {
    // 400 Bad Request - Snapshot
    INVALID_REQUEST_PARAMETER(400, "ACC-STORAGE-SNAPSHOT-INVALID-PARAM", "필수 요청 파라미터가 누락되었거나 유효하지 않습니다."),
    INVALID_SNAPSHOT_ID(400, "ACC-STORAGE-SNAPSHOT-ID-INVALID", "스냅샷 ID 형식이 유효하지 않습니다 (UUID)."),
    INVALID_SNAPSHOT_NAME(400, "ACC-STORAGE-SNAPSHOT-NAME-INVALID", "스냅샷 이름은 1~128자여야 하며, 영문/중문으로 시작하고 [0-9, a-z, A-Z, \"-_()[].:^\"] 문자만 포함할 수 있습니다."),

    // 400 Bad Request - Volume
    INVALID_VOLUME_ID(400, "ACC-STORAGE-VOLUME-ID-INVALID", "볼륨 ID 형식이 유효하지 않습니다 (UUID)."),
    INVALID_VOLUME_NAME(400, "ACC-STORAGE-VOLUME-NAME-INVALID", "볼륨 이름은 1~128자여야 하며, 영문/중문으로 시작하고 [0-9, a-z, A-Z, \"-_()[].:^\"] 문자만 포함할 수 있습니다."),
    INVALID_VOLUME_SIZE(400, "ACC-STORAGE-VOLUME-SIZE-INVALID", "볼륨 크기는 1 이상의 정수여야 합니다."),

    // 403 Forbidden (권한 없음)
    FORBIDDEN_ACCESS(403, "ACC-STORAGE-FORBIDDEN", "해당 프로젝트의 스토리지 리소스에 접근할 권한이 없습니다."),

    // 404 Not Found - Snapshot
    SNAPSHOT_NOT_FOUND(404, "ACC-STORAGE-SNAPSHOT-NOT-FOUND", "요청한 볼륨 스냅샷 ID를 찾을 수 없습니다."),
    MARKER_NOT_FOUND(404, "ACC-STORAGE-MARKER-NOT-FOUND", "목록 조회의 기준(marker)이 되는 ID를 찾을 수 없습니다."),

    // 404 Not Found - Volume
    VOLUME_NOT_FOUND(404, "ACC-STORAGE-VOLUME-NOT-FOUND", "요청한 볼륨 ID를 찾을 수 없습니다."),

    // 409 Conflict
    SNAPSHOT_IN_USE(409, "ACC-STORAGE-SNAPSHOT-CONFLICT", "스냅샷이 사용 중이거나 삭제/생성이 불가능한 상태입니다."),
    VOLUME_IN_USE(409, "ACC-STORAGE-VOLUME-CONFLICT", "볼륨이 사용 중이거나 삭제/생성이 불가능한 상태입니다."),
    VOLUME_QUOTA_EXCEEDED(409, "ACC-STORAGE-VOLUME-QUOTA-EXCEEDED", "볼륨 생성 쿼터를 초과했습니다."),

    // 500 Internal Server Error
    CINDER_API_FAILURE(500, "ACC-STORAGE-CINDER-API-FAILURE", "OpenStack Cinder API 통신 중 오류가 발생하였습니다."),

    // 400 Bad Request - Snapshot Policy
    INVALID_POLICY_ID(400, "ACC-STORAGE-POLICY-ID-INVALID", "정책 ID 형식이 유효하지 않습니다."),
    INVALID_POLICY_NAME(400, "ACC-STORAGE-POLICY-NAME-INVALID", "정책 이름은 1~100자여야 합니다."),
    INVALID_INTERVAL_TYPE(400, "ACC-STORAGE-INTERVAL-TYPE-INVALID", "간격 타입은 DAILY, WEEKLY, MONTHLY 중 하나여야 합니다."),
    INVALID_SCHEDULE_PARAMETER(400, "ACC-STORAGE-SCHEDULE-PARAM-INVALID", "간격 타입에 맞는 스케줄 파라미터가 필요합니다."),

    // 404 Not Found - Snapshot Policy
    POLICY_NOT_FOUND(404, "ACC-STORAGE-POLICY-NOT-FOUND", "요청한 스냅샷 수명관리자 정책을 찾을 수 없습니다."),

    // 409 Conflict - Snapshot Policy
    POLICY_ALREADY_EXISTS(409, "ACC-STORAGE-POLICY-EXISTS", "동일한 볼륨에 대한 정책이 이미 존재합니다.");

    private final int status;
    private final String code;
    private final String message;

    VolumeErrorCode(int status, String code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }
}
