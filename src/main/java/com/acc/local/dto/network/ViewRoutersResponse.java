package com.acc.local.dto.network;

import com.fasterxml.jackson.annotation.JsonInclude;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Setter
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "라우터 정보")
public class ViewRoutersResponse {

    @Schema(description = "라우터 이름", example = "my-router")
    String routerName;
    @Schema(description = "라우터 ID", example = "123e4567-e89b-12d3-a456-426614174000")
    String routerId;
    @Schema(description = "라우터 상태", example = "ACTIVE")
    String status;
    @Schema(description = "외부 연결 여부", example = "true")
    Boolean isExternal;
    @Schema(description = "외부 네트워크 이름", example = "my-external-network")
    String externalNetworkName;
    @Schema(description = "외부 IP", example = "192.168.0.1")
    String externalIp;
    @Schema(description = "생성일시", example = "2021-01-01T00:00:00Z")
    String createdAt;

}
