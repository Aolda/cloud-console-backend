package com.acc.local.dto.auth;

import com.acc.local.domain.model.auth.KeystoneUser;
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
    Integer projectLimit
) {
    public static GetUserResponse from(KeystoneUser keystoneUser) {
        return GetUserResponse.builder()
                .id(keystoneUser.getId())
                .name(keystoneUser.getName())
                .domainId(keystoneUser.getDomainId())
                .defaultProjectId(keystoneUser.getDefaultProjectId())
                .enabled(keystoneUser.isEnabled())
                .federated(keystoneUser.getFederated())
                .links(keystoneUser.getLinks())
                .passwordExpiresAt(keystoneUser.getPasswordExpiresAt())
                .email(keystoneUser.getEmail())
                .description(keystoneUser.getDescription())
                .options(keystoneUser.getOptions())
                // TODO: API 개발 시, 확인 필요
                //.projectLimit(user.getProjectLimit())
                .build();
    }
}