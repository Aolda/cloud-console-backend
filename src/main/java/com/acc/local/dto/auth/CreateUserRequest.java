package com.acc.local.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;



public record CreateUserRequest(
        @Schema(description = "사용자에게 만들어줄 계정 아이디") String userEmail,
        @Schema(description = "사용자에게 만들어줄 계정 비밀번호") String userPassword,
        @Schema(description = "firstName (이름)") String firstName,
        @Schema(description = "last Name (성)") String lastName,
        @Schema(description = "keycloak에 저장될 유저이름") String userName
) { }
