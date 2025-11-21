package com.acc.local.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;

/**
 * 관리자 사용자 수정 요청 DTO
 */
@Builder
public record AdminUpdateUserRequest(

    @NotBlank
    @Schema(description = "사용자 이름")
    String username,

    @NotBlank
    @Schema(description = "사용자 이메일")
    String email,

    @NotBlank
    @Schema(description = "학과")
    String department,

    @NotBlank
    @Pattern(regexp = "^\\d{7}$", message = "학번은 7개의 문자로 이뤄져야합니다.")
    @Schema(description = "학번")
    String studentId,

    @NotBlank
    @Schema(description = "비밀번호")
    String password,

    @NotBlank
    @Pattern(regexp = "^01[0-9]-\\d{4}-\\d{4}$", message = "올바른 전화번호 형식이어야 합니다.")
    @Schema(description = "전화번호")
    String phoneNumber,

    @NotBlank
    @Schema(description = "계정 활성화 여부")
    Boolean isEnabled
) { }
