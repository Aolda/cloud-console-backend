package com.acc.local.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

/**
 * 관리자 사용자 생성 응답 DTO
 */
@Builder
public record AdminCreateUserResponse(
    @Schema(description = "사용자 ID")
    String userId
) {
    public static AdminCreateUserResponse from(String userId) {
        return AdminCreateUserResponse.builder()
                .userId(userId)
                .build();
    }
}