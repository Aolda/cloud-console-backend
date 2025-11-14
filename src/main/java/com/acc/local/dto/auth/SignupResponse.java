package com.acc.local.dto.auth;

/**
 * 회원가입 응답 DTO
 */
public record SignupResponse(
    String userId
) {
    public static SignupResponse from(String userId) {
        return new SignupResponse(userId);
    }
}
