package com.acc.local.dto.auth;

import jakarta.validation.constraints.NotBlank;

public record ProjectTokenRequest(
    @NotBlank(message = "프로젝트 ID는 필수입니다")
    String projectId
) {
}