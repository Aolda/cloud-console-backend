package com.acc.local.dto.network;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class CreateInterfaceRequest {

    @Schema(description = """
            인터페이스 이름
            
            - 영문 대소문자, 숫자, '-', '_', '(', ')', '[', ']', '.', ':', '^' 문자 사용 가능
            """,
            example = "my-interface",
            requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank
    @Pattern(regexp = "^[a-zA-Z][0-9a-zA-Z\\-_()\\[\\]\\.:^]{0,127}$")
    String interfaceName;

    @Schema(description = "인터페이스 설명",
            example = "This is my interface",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    String description;

    @Schema(description = "소속 네트워크 ID",
            example = "abc12-asdfasdf-asdfasdf-asdfad",
            requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank
    String networkId;

    @Schema(description = """
            소속 서브넷 ID
            
            - nullable
            - 지정하지 않을 경우 네트워크의 기본 서브넷에 연결됩니다.
            """,
            example = "asdf-asdfasdf-adsfasdf-adf",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    String subnetId;

    @Schema(description = "보안 그룹 ID 목록",
            example = """
                    [
                        "asdf-asdfasdf-adsfasdf-adf",
                        "qwer-qwerqwer-qwerqwer-qwer"
                    ]
                    """,
            requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank
    List<String> securityGroupIds;

    @Schema(description = "외부 네트워크 연결 여부",
            example = "true",
            requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull
    Boolean isExternal;

}
