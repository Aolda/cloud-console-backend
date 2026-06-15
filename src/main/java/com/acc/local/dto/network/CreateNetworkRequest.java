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
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @Min(value = 68)
    @Max(value = 65535)
    Integer mtu;

    @Schema(description = "서브넷 목록 \n\n " +
            "- nullable",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    List<CreateSubnetRequest> subnets;
}
