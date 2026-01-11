package com.acc.local.dto.auth;

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

    // ACC 내부 정보
    String department,
    String phoneNumber,
    Integer projectLimit
) {
    public static UpdateUserResponse from(UserKeystoneDto userKeystoneDto) {
        return UpdateUserResponse.builder()
                .userId(userKeystoneDto.id())
                .name(userKeystoneDto.name())
                .domainId(userKeystoneDto.domainId())
                .defaultProjectId(userKeystoneDto.defaultProjectId())
                .enabled(userKeystoneDto.enabled())
                .email(userKeystoneDto.email())
                .description(userKeystoneDto.description())
                .build();
    }
}