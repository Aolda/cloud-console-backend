package com.acc.local.dto.network;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
public class CreateRouterRequest {

    @Schema(description = "라우터 이름", example = "my-router")
    String routerName;

    @Schema(description = "네트워크 설명", example = "This is my router")
    String description;

    @Schema(description = "provider 연결 여부", example = "true")
    Boolean gateway;

}
