package com.acc.local.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
public class CreateUserRequest {

    @Schema(description = "사용자에게 만들어줄 계정 아이디")
    private String userEmail;
    @Schema(description = "사용자에게 만들어줄 계정 비밀번호")
    private String userPassword;
    @Schema(description = "firstName (이름)")
    private String firstName;
    @Schema(description = "last Name (성)")
    private String lastName;
    @Schema(description = "keycloak에 저장될 유저이름")
    private String userName;
}
