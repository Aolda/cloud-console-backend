package com.acc.local.dto.network;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;

@Getter
public class CreateSecurityGroupRequest {

    @Schema(description = "보안 그룹 이름", example = "my-security-group")
    @NotBlank
    @Pattern(regexp = "^[a-zA-Z][0-9a-zA-Z\\-_()\\[\\]\\.:^]{0,127}$")
    private String securityGroupName;
    @Schema(description = "보안 그룹 설명", example = "This is my security group")
    private String description;
}
