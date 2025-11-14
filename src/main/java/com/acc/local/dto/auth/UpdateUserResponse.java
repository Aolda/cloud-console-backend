package com.acc.local.dto.auth;

import com.acc.local.domain.model.auth.User;
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
    public static UpdateUserResponse from(User user) {
        return UpdateUserResponse.builder()
                .userId(user.getId())
                .name(user.getName())
                .domainId(user.getDomainId())
                .defaultProjectId(user.getDefaultProjectId())
                .enabled(user.isEnabled())
                .email(user.getEmail())
                .description(user.getDescription())
                // ACC 내부 정보
                .department(user.getDepartment())
                .phoneNumber(user.getPhoneNumber())
                // TODO: API 개발 시, 확인 필요
                //.projectLimit(user.getProjectLimit())
                .build();
    }
}