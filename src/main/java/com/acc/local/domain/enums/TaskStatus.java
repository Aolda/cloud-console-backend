package com.acc.local.domain.enums;

/**
 * 스냅샷 작업 실행 상태.
 *
 * PENDING - 대기 중 또는 아직 시작되지 않음
 * RUNNING - 작업 실행 중
 * SUCCESS - 작업 성공적으로 완료
 * FAILED  - 작업 실패
 */
public enum TaskStatus {
    PENDING,
    RUNNING,
    SUCCESS,
    FAILED
}

