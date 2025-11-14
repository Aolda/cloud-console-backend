package com.acc.local.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public record CreateUserRequest(
        @NotNull @Schema(description = "사용자에게 만들어줄 계정 아이디") String userEmail,
        @NotNull @Schema(description = "사용자에게 만들어줄 계정 비밀번호") String userPassword,
        @NotNull @Schema(description = "firstName (이름)") String firstName,
        @NotNull @Schema(description = "last Name (성)") String lastName,
        @NotNull @Schema(description = "keycloak에 저장될 유저이름") String userName,

        // ACC 내부 정보
        @NotNull @Schema(description = "학과") String department,
        @NotNull @Schema(description = "전화번호") String phoneNumber,
        @Schema(description = "프로젝트 제한 수") Integer projectLimit
) { }
