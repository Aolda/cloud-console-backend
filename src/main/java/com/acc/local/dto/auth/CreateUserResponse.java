package com.acc.local.dto.auth;

import com.acc.local.domain.model.auth.User;
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
    public static CreateUserResponse from(User user) {
        return CreateUserResponse.builder()
                .userId(user.getId())
                .userName(user.getName())
                .defaultProjectId(user.getDefaultProjectId())
                .domainId(user.getDomainId())
                .email(user.getEmail())
                .enabled(user.isEnabled())
                // ACC 내부 정보
                .department(user.getDepartment())
                .phoneNumber(user.getPhoneNumber())
                // TODO: API 개발 시, 확인 필요
                //.projectLimit(user.getProjectLimit())
                .build();
    }
}
