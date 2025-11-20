package com.acc.local.dto.network;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class CreateInterfaceRequest {

    @Schema(description = "인터페이스 이름", example = "my-interface")
    String interfaceName;

    @Schema(description = "인터페이스 설명", example = "This is my interface")
    String description;

    @Schema(description = "소속 네트워크 ID", example = "abc123def456")
    String networkId;

    @Schema(description = "소속 서브넷 ID", example = "subnet789ghi012")
    String subnetId;

    @Schema(description = "보안 그룹 ID 목록")
    List<String> securityGroupIds;

    @Schema(description = "외부 네트워크 연결 여부", example = "true")
    Boolean external;

}
