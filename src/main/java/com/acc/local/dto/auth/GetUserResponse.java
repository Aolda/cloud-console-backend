package com.acc.local.dto.auth;

import lombok.Builder;

import java.util.List;
import java.util.Map;

@Builder
public record GetUserResponse(
    String id,
    String name,
    String domainId,
    String defaultProjectId,
    boolean enabled,
    List<Map<String, Object>> federated,
    Map<String, String> links,
    String passwordExpiresAt,
    String email,
    String description,
    Map<String, Object> options,

    // ACC 내부 정보
    String department,
    String phoneNumber,
    Integer projectLimit
) {
    public static GetUserResponse from(UserKeystoneDto userKeystoneDto) {
        return GetUserResponse.builder()
                .id(userKeystoneDto.id())
                .name(userKeystoneDto.name())
                .domainId(userKeystoneDto.domainId())
                .defaultProjectId(userKeystoneDto.defaultProjectId())
                .enabled(userKeystoneDto.enabled())
                .federated(userKeystoneDto.federated())
                .links(userKeystoneDto.links())
                .passwordExpiresAt(userKeystoneDto.passwordExpiresAt())
                .email(userKeystoneDto.email())
                .description(userKeystoneDto.description())
                .options(userKeystoneDto.options())
                .build();
    }
}