package com.acc.local.domain.enums.outbox;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Outbox 이벤트 타입
 */
@Getter
@RequiredArgsConstructor
public enum EventType {
    // Project Request Events
    PROJECT_REQUEST_CREATED("ProjectRequestCreated", "프로젝트 요청 생성"),
    PROJECT_REQUEST_APPROVED("ProjectRequestApproved", "프로젝트 요청 승인"),
    PROJECT_REQUEST_REJECTED("ProjectRequestRejected", "프로젝트 요청 거부"),

    // Project Events
    PROJECT_CREATED("ProjectCreated", "프로젝트 생성"),
    PROJECT_DELETED("ProjectDeleted", "프로젝트 삭제");

    private final String code;
    private final String description;
}

