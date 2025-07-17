package com.acc.local.domain.enums;

public enum UserStatus {
    PENDING("생성 대기"),
    ACTIVE("활성"),
    SUSPENDED("정지"),
    INACTIVE("비활성"),
    DELETED("삭제됨");

    private final String description;

    UserStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
