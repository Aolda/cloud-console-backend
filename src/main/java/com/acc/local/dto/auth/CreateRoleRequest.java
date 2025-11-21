package com.acc.local.dto.auth;

import lombok.Builder;

@Builder
public record CreateRoleRequest(
        String name,
        String description,
        String domainId
) {
}
