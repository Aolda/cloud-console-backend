package com.acc.local.dto.auth;

public record ListRolesResponse(
        String roleId,
        String name,
        String description,
        String domainId
) {
    public static ListRolesResponse from(String roleId, String name, String description, String domainId) {
        return new ListRolesResponse(roleId, name, description, domainId);
    }
}
