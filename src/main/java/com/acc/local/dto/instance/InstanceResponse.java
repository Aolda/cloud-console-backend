package com.acc.local.dto.instance;

import com.acc.local.domain.enums.InstanceStatus;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class InstanceResponse {

    @Schema(description = "인스턴스 이름", example = "my-vm-server")
    String instanceName;

    @Schema(description = "인스턴스 고유 ID", example = "vm-uuid-1234-5678")
    String instanceId;

    @Schema(description = "인스턴스 타입(Flavor) 이름", example = "a1.standard")
    String type;

    @Schema(description = "인스턴스 현재 상태", example = "활성")
    InstanceStatus status;

    @Schema(description = "인스턴스 이미지 이름", example = "Ubuntu 22.04 LTS")
    String image;

    @Schema(description = "내부 IP 주소 목록", example = "[\"192.168.0.10\"]")
    List<String> internalIps;

    @Schema(description = "외부(Floating) IP 주소 목록", example = "[\"10.20.30.40\"]")
    List<String> externalIps;
}
