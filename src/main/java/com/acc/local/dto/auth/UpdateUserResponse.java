package com.acc.local.dto.auth;

import com.acc.local.domain.model.auth.KeystoneUser;
import lombok.Builder;

@Builder
public record UpdateUserResponse(
    String userId,
    String name,
    String domainId,
    String defaultProjectId,
    boolean enabled,
    String email,
    String description,

    Integer projectLimit
) {
    public static UpdateUserResponse from(KeystoneUser keystoneUser) {
        return UpdateUserResponse.builder()
                .userId(keystoneUser.getId())
                .name(keystoneUser.getName())
                .domainId(keystoneUser.getDomainId())
                .defaultProjectId(keystoneUser.getDefaultProjectId())
                .enabled(keystoneUser.isEnabled())
                .email(keystoneUser.getEmail())
                .description(keystoneUser.getDescription())
                // TODO: API 개발 시, 확인 필요
                //.projectLimit(user.getProjectLimit())
                .build();
    }
}