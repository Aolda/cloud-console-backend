package com.acc.local.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

/**
 * 관리자 사용자 상세 조회 응답 DTO
 */
@Builder
public record AdminGetUserResponse(
    @Schema(description = "사용자 ID")
    String userId,

    @Schema(description = "사용자 이름")
    String username,

    @Schema(description = "이메일")
    String email,

    @Schema(description = "학과")
    String department,

    @Schema(description = "학번")
    String studentId,

    @Schema(description = "전화번호")
    String phoneNumber,

    @Schema(description = "계정 활성화 여부")
    Boolean isEnabled,

    @Schema(description = "관리자 여부")
    Boolean isAdmin,

    @Schema(description = "삭제 상태 여부")
    Boolean isDeleted
) { }
