package com.acc.local.dto.auth;

import com.acc.local.domain.enums.auth.AuthType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;

/**
 * 관리자 사용자 생성 요청 DTO
 */
@Builder
public record AdminCreateUserRequest(
    @NotBlank(message = "사용자명은 필수입니다")
    @Schema(description = "사용자 이름")
    String username,

    @NotBlank(message = "이메일은 필수입니다")
    @Email(message = "올바른 이메일 형식이어야 합니다")
    @Pattern(regexp = "^[A-Za-z0-9+_.-]+@ajou\\.ac\\.kr$", message = "이메일은 @ajou.ac.kr 도메인이어야 합니다")
    @Schema(description = "사용자 이메일")
    String email,

    @NotBlank(message = "학과는 필수입니다")
    @Schema(description = "학과")
    String department,

    @NotBlank(message = "학번은 필수입니다")
    @Pattern(regexp = "^\\d{7}$", message = "학번은 7개의 문자로 이뤄져야합니다.")
    @Schema(description = "학번")
    String studentId,

    @NotBlank(message = "비밀번호는 필수입니다")
    @Schema(description = "비밀번호")
    String password,

    @NotBlank(message = "전화번호는 필수입니다")
    @Pattern(regexp = "^01[0-9]-\\d{4}-\\d{4}$", message = "올바른 전화번호 형식이어야 합니다.")
    @Schema(description = "전화번호")
    String phoneNumber,

    @NotNull(message = "계정 활성화 여부는 필수입니다")
    @Schema(description = "계정 활성화 여부")
    Boolean isEnabled,

    @NotNull(message = "관리자 여부는 필수입니다")
    @Schema(description = "관리자 여부")
    Boolean isAdmin,

    @NotNull(message = "인증 타입은 필수입니다")
    @Schema(description = "인증 타입")
    AuthType authType
) { }