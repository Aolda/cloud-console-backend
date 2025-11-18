package com.acc.local.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

/**
 * 관리자 사용자 목록 조회 응답 DTO (단일 사용자)
 */
@Builder
public record AdminListUsersResponse(
    @Schema(description = "사용자 ID")
    String userId,

    @Schema(description = "사용자 이름")
    String username,

    @Schema(description = "관리자 여부")
    Boolean isAdmin,

    @Schema(description = "이메일")
    String email,

    @Schema(description = "전화번호")
    String phoneNumber,

    @Schema(description = "학과")
    String department,

    @Schema(description = "계정 활성화 여부")
    Boolean enabled,

    @Schema(description = "기본 프로젝트 이름")
    String defaultProjectName
) { }
