package com.acc.local.dto.network;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
public class CreateSecurityGroupRequest {

    @Schema(description = "보안 그룹 이름", example = "my-security-group")
    private String securityGroupName;
    @Schema(description = "보안 그룹 설명", example = "This is my security group")
    private String description;
}
