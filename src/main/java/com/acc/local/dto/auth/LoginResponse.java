package com.acc.local.dto.auth;

/**
 * 로그인 응답 DTO
 * Access Token만 Body에 담아 반환 (Refresh Token은 Cookie로 전달)
 */
public record LoginResponse(
    String accessToken
) {
}