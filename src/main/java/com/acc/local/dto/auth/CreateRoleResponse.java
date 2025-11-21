package com.acc.local.dto.auth;

public record CreateRoleResponse(
        String roleId,
        String name,
        String description,
        String domainId
) {
    public static CreateRoleResponse from(String roleId, String name, String description, String domainId) {
        return new CreateRoleResponse(roleId, name, description, domainId);
    }
}