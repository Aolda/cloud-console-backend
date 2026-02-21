package com.acc.local.domain.enums.notification;

/**
 * 프로젝트 알림 타입
 */
public enum ProjectNotificationType {
    /**
     * 프로젝트 요청 생성
     */
    PROJECT_REQUEST_CREATED,

    /**
     * 프로젝트 요청 승인
     */
    PROJECT_REQUEST_APPROVED,

    /**
     * 프로젝트 요청 거부
     */
    PROJECT_REQUEST_REJECTED,

    /**
     * 프로젝트 생성 (관리자가 직접 생성)
     */
    PROJECT_CREATED
}

