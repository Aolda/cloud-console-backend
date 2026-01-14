package com.acc.local.dto.network;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;

@Getter
public class CreateSecurityGroupRequest {

    @Schema(description = """
            보안 그룹 이름
            
            - 영문 대소문자, 숫자, '-', '_', '(', ')', '[', ']', '.', ':', '^' 문자 사용 가능
            """,
            example = "my-security-group",
            requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank
    @Pattern(regexp = "^[a-zA-Z][0-9a-zA-Z\\-_()\\[\\]\\.:^]{0,127}$")
    private String securityGroupName;
    @Schema(description = """
            보안 그룹 설명
            
            - nullable
            """,
            example = "This is my security group",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String description;
}
