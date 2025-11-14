package com.acc.local.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;

public record UpdateUserRequest(
        @Schema(description = "사용자 이름") String userName,
        @Schema(description = "사용자 이메일") String userEmail,
        @Schema(description = "설명") String description,
        @Schema(description = "활성화 여부") Boolean enabled,
        @Schema(description = "기본 프로젝트 ID") String defaultProjectId,

        // ACC 내부 정보 (Update는 선택 사항)
        @Schema(description = "학과") String department,
        @Schema(description = "전화번호") String phoneNumber,
        @Schema(description = "프로젝트 제한 수") Integer projectLimit
) { }