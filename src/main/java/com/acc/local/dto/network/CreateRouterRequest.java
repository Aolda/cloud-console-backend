package com.acc.local.dto.network;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;

@Getter
public class CreateRouterRequest {

    @Schema(description = "라우터 이름", example = "my-router")
    @NotBlank
    @Pattern(regexp = "^[a-zA-Z][0-9a-zA-Z\\-_()\\[\\]\\.:^]{0,127}$")
    String routerName;

    @Schema(description = "네트워크 설명", example = "This is my router")
    String description;

    @Schema(description = "provider 연결 여부", example = "true")
    @NotBlank
    Boolean isExternal;

}
