package com.acc.local.dto.auth;

import lombok.Builder;

@Builder
public record CreateUserResponse(
    String userId,
    String userName,
    String defaultProjectId,
    String domainId,
    String email,
    boolean enabled,

    // ACC 내부 정보
    String department,
    String phoneNumber,
    Integer projectLimit
) {
    public static CreateUserResponse from(UserKeystoneDto userKeystoneDto) {
        return CreateUserResponse.builder()
                .userId(userKeystoneDto.id())
                .userName(userKeystoneDto.name())
                .defaultProjectId(userKeystoneDto.defaultProjectId())
                .domainId(userKeystoneDto.domainId())
                .email(userKeystoneDto.email())
                .enabled(userKeystoneDto.enabled())
                .build();
    }
}
