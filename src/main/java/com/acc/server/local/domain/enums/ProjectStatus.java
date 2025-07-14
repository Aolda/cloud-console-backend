package com.acc.server.local.domain.enums;

public enum ProjectStatus {
    PENDING("생성 대기"),      // Keystone 생성 전
    ACTIVE("활성"),
    INACTIVE("비활성"),
    SUSPENDED("정지"),
    DELETED("삭제됨");

    private final String description;

    ProjectStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
