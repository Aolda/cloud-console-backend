package com.acc.local.dto.auth;

import com.acc.local.domain.model.auth.KeystoneUser;
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
    public static CreateUserResponse from(KeystoneUser keystoneUser) {
        return CreateUserResponse.builder()
                .userId(keystoneUser.getId())
                .userName(keystoneUser.getName())
                .defaultProjectId(keystoneUser.getDefaultProjectId())
                .domainId(keystoneUser.getDomainId())
                .email(keystoneUser.getEmail())
                .enabled(keystoneUser.isEnabled())
                // ACC 내부 정보
                .department(keystoneUser.getDepartment())
                .phoneNumber(keystoneUser.getPhoneNumber())
                // TODO: API 개발 시, 확인 필요
                //.projectLimit(user.getProjectLimit())
                .build();
    }
}
