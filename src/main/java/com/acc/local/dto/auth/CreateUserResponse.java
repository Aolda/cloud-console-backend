package com.acc.local.dto.auth;

import com.acc.local.domain.model.User;
import lombok.Builder;

@Builder
public record CreateUserResponse(
    String userId,
    String userName,
    String defaultProjectId,
    String domainId,
    String email,
    boolean enabled
) {
    public static CreateUserResponse from(User user) {
        return CreateUserResponse.builder()
                .userId(user.getId())
                .userName(user.getName())
                .defaultProjectId(user.getDefaultProjectId())
                .domainId(user.getDomainId())
                .email(user.getEmail())
                .enabled(user.isEnabled())
                .build();
    }
}
