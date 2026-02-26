package com.acc.local.domain.enums.outbox;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Outbox 이벤트의 Aggregate 타입
 */
@Getter
@RequiredArgsConstructor
public enum AggregateType {
    PROJECT_REQUEST("ProjectRequest", "프로젝트 요청"),
    PROJECT("Project", "프로젝트");

    private final String code;
    private final String description;
}

