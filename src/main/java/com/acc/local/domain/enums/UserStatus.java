package com.acc.local.domain.enums;

public enum UserStatus {
    ACTIVE("활성"),
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
