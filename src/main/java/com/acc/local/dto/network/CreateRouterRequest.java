package com.acc.local.dto.network;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;

@Getter
public class CreateRouterRequest {

    @Schema(description = """
            라우터 이름
            
            - 영문 대소문자, 숫자, '-', '_', '(', ')', '[', ']', '.', ':', '^' 문자 사용 가능
            """,
            example = "my-router",
            requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank
    @Pattern(regexp = "^[a-zA-Z][0-9a-zA-Z\\-_()\\[\\]\\.:^]{0,127}$")
    String routerName;

    @Schema(description = "네트워크 설명",
            example = "This is my router",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    String description;

    @Schema(description = "외부 연결 여부",
            example = "true",
            requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank
    Boolean isExternal;

}
