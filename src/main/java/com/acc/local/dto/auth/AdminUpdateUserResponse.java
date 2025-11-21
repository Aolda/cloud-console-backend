package com.acc.local.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

/**
 * 관리자 사용자 수정 응답 DTO
 */
@Builder
public record AdminUpdateUserResponse(
    @Schema(description = "사용자 ID")
    String userId
) {
    public static AdminUpdateUserResponse from(String userId) {
        return AdminUpdateUserResponse.builder()
                .userId(userId)
                .build();
    }
}
