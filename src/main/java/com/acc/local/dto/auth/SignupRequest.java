package com.acc.local.dto.auth;

import com.acc.local.domain.enums.auth.AuthType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * 회원가입 요청 DTO
 */
public record SignupRequest(
    @NotBlank(message = "사용자명은 필수입니다")
    String username,

    @NotBlank(message = "이메일은 필수입니다")
    @Email(message = "올바른 이메일 형식이어야 합니다")
    String email,

    @NotBlank(message = "비밀번호는 필수입니다")
    String password,

    @NotBlank(message = "학과는 필수입니다")
    String department,

    @NotBlank(message = "학번은 필수입니다")
    String studentId,

    @NotBlank(message = "전화번호는 필수입니다")
    String phoneNumber,

    @NotNull(message = "인증 타입은 필수입니다")
    AuthType authType
) {
    public SignupRequest {
        // authType이 null이면 기본값 GOOGLE 설정
        if (authType == null) {
            authType = AuthType.GOOGLE;
        }
    }
}
