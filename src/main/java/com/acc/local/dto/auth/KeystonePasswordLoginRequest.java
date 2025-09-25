package com.acc.local.dto.auth;

import jakarta.validation.constraints.NotBlank;

public record KeystonePasswordLoginRequest(
    @NotBlank(message = "사용자명은 필수입니다")
    String username,

    @NotBlank(message = "패스워드는 필수입니다")
    String password,

    String domainName
) {
    public KeystonePasswordLoginRequest {
        if (domainName == null || domainName.isBlank()) {
            domainName = "Default";
        }
    }
}