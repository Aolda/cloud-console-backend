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
@Schema(description = "서브넷 정보")
public class ViewSubnetsResponse {


    @Schema(description = "서브넷 ID",
            example = "123e4567-e89b-12d3-a456-426614174000",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private String subnetId;

    @Schema(description = "서브넷 이름",
            example = "my-subnet",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private String subnetName;

    @Schema(description = "소속된 네트워크 ID",
            example = "123e4567-e89b-12d3-a456-426614174000",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private String networkId;

    @Schema(description = "서브넷 CIDR",
            example = "192.168.0.0/24",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private String cidr;

    @Schema(description = "서브넷 게이트웨이",
            example = "192.168.0.1",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private String gatewayIp;

    @Schema(description = """
            생성일시
            
            - ISO 8601 형식
            """,
            example = "2021-01-01T00:00:00Z",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private String createdAt;

    @Schema(description = "서브넷 설명",
            example = "This is my subnet",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String description;

}
