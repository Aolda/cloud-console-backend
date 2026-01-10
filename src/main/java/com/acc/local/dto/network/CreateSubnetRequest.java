package com.acc.local.dto.network;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CreateSubnetRequest {

    @Schema(description = "서브넷 이름 \n\n " +
            "- 영문 대소문자, 숫자, '-', '_', '(', ')', '[', ']', '.', ':', '^' 문자 사용 가능",
            example = "my-subnet",
            requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank
    @Pattern(regexp = "^[a-zA-Z][0-9a-zA-Z\\-_()\\[\\]\\.:^]{0,127}$")
    private String subnetName;

    @Schema(description = "서브넷 CIDR \n\n " +
            "- [0-255].[0-255].[0-255].[0-255]/[1-32] 형식",
            example = "192.168.0.0/24",
            requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank
    @Pattern(regexp = "^((25[0-5]|2[0-4][0-9]|1[0-9]{2}|[1-9][0-9]|[0-9])\\." +
            "){3}(25[0-5]|2[0-4][0-9]|1[0-9]{2}|[1-9][0-9]|[0-9])\\/" +
            "(?:[1-9]|[12]\\d|3[0-2])$")
    private String cidr;

    @Schema(description = """
            서브넷 게이트웨이 IP
            
            - [0-255].[0-255].[0-255].[0-255] 형식
            - nullable
            """,
            example = "192.168.0.1",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @Pattern(regexp = "^((25[0-5]|2[0-4][0-9]|1[0-9]{2}|[1-9][0-9]|[0-9])\\." +
            "){3}(25[0-5]|2[0-4][0-9]|1[0-9]{2}|[1-9][0-9]|[0-9])$")
    private String gatewayIp;

    @Schema(description = """
            서브넷 설명
            
            - nullable
            """,
            example = "This is my subnet",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String description;
}
