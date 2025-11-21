package com.acc.local.domain.enums;

/**
 * 스냅샷 정책 실행 간격 타입.
 *
 * DAILY   - 매일 지정된 시간에 실행
 * WEEKLY  - 매주 지정된 요일/시간에 실행
 * MONTHLY - 매월 지정된 일자/시간에 실행
 */
public enum IntervalType {
    DAILY,
    WEEKLY,
    MONTHLY
}

