package com.acc.local.dto.auth;

import jakarta.validation.constraints.NotBlank;

/**
 * Refresh Token 요청 DTO
 * 쿠키에서 전달받은 Refresh Token으로 새로운 Access Token 발급 요청
 */
public record RefreshTokenRequest(
    @NotBlank(message = "Refresh Token은 필수입니다")
    String refreshToken
) {
}