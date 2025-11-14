package com.acc.local.dto.auth;

import lombok.Builder;

/**
 * 로그인 시 발급되는 토큰 정보
 */
@Builder
public record LoginTokens(
    String accessToken,
    String refreshToken
) {
    public static LoginTokens from(String accessToken, String refreshToken) {
        return LoginTokens.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }
}