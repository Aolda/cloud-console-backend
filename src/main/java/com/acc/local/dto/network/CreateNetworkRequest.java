package com.acc.local.dto.network;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
public class CreateNetworkRequest {

    @Schema(description = "네트워크 이름 \n\n " +
            "- 영문 대소문자, 숫자, '-', '_', '(', ')', '[', ']', '.', ':', '^' 문자 사용 가능",
            example = "my-network",
            requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank
    @Pattern(regexp = "^[a-zA-Z][0-9a-zA-Z\\-_()\\[\\]\\.:^]{0,127}$")
    String networkName;

    @Schema(description = "네트워크 설명 \n\n " +
            "- nullable",
            example = "This is my network",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    String description;

    @Schema(description = "MTU 값 \n\n " +
            "- 범위: 68 ~ 65535",
            example = "1450",
            defaultValue = "1450",
            requiredMode = Schema.RequiredMode.REQUIRED)
    @Min(value = 68)
    @Max(value = 65535)
    Integer mtu = 1450;

    @Schema(description = "서브넷 목록 \n\n " +
            "- nullable",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    List<Subnet> subnets;

    @Getter
    @Builder
    @Schema(description = "서브넷 정보")
    public static class Subnet {

        @Schema(description = "서브넷 CIDR \n\n " +
                "- [0-255].[0-255].[0-255].[0-255]/[1-32] 형식",
                example = "192.168.0.0/24",
                requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank
        @Pattern(regexp = "^((25[0-5]|2[0-4][0-9]|1[0-9]{2}|[1-9][0-9]|[0-9])\\." +
                                "){3}(25[0-5]|2[0-4][0-9]|1[0-9]{2}|[1-9][0-9]|[0-9])\\/" +
                                "(?:[1-9]|[12]\\d|3[0-2])$")
        String cidr;

        @Schema(description = "서브넷 이름 \n\n " +
                "- 영문 대소문자, 숫자, '-', '_', '(', ')', '[', ']', '.', ':', '^' 문자 사용 가능",
                example = "my-subnet",
                requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank
        @Pattern(regexp = "^[a-zA-Z][0-9a-zA-Z\\-_()\\[\\]\\.:^]{0,127}$")
        String subnetName;
    }
}
