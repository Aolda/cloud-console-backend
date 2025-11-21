package com.acc.local.dto.network;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
public class CreateSecurityRuleRequest {

    @Schema(description = "프로토콜", example = "tcp")
    private String protocol;

    @Schema(description = "방향", example = "ingress or egress")
    private String direction;

    @Schema(description = "포트", example = "80")
    private Integer port;

    @Schema(description = "허용 보안 그룹 ID", example = "sg-12345678")
    private String remoteSecurityGroupId;

    @Schema(description = "허용 CIDR", example = "192.168.0.0/24")
    private String cidr;
}
