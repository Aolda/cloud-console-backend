package com.acc.local.dto.auth;

import com.acc.local.domain.model.User;
import lombok.Builder;

@Builder
public record UpdateUserResponse(
    String userId,
    String name,
    String domainId,
    String defaultProjectId,
    boolean enabled,
    String email,
    String description
) {
    public static UpdateUserResponse from(User user) {
        return UpdateUserResponse.builder()
                .userId(user.getId())
                .name(user.getName())
                .domainId(user.getDomainId())
                .defaultProjectId(user.getDefaultProjectId())
                .enabled(user.isEnabled())
                .email(user.getEmail())
                .description(user.getDescription())
                .build();
    }
}