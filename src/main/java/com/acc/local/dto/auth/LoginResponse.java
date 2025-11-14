package com.acc.local.dto.auth;

import lombok.Builder;

/**
 * 로그인 응답 DTO
 * Access Token만 Body에 담아 반환 (Refresh Token은 Cookie로 전달)
 */
@Builder
public record LoginResponse(
    String accessToken
) {
    public static LoginResponse from(String accessToken) {
        return LoginResponse.builder()
                .accessToken(accessToken)
                .build();
    }
}