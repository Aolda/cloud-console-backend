package com.acc.local.dto.auth;

import com.acc.local.domain.model.User;
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
    Map<String, Object> options
) {
    public static GetUserResponse from(User user) {
        return GetUserResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .domainId(user.getDomainId())
                .defaultProjectId(user.getDefaultProjectId())
                .enabled(user.isEnabled())
                .federated(user.getFederated())
                .links(user.getLinks())
                .passwordExpiresAt(user.getPasswordExpiresAt())
                .email(user.getEmail())
                .description(user.getDescription())
                .options(user.getOptions())
                .build();
    }
}