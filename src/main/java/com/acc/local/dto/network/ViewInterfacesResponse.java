package com.acc.local.dto.network;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Setter
@Getter
@Schema(description = "인터페이스 정보")
public class ViewInterfacesResponse {

    @Schema(description = "인터페이스 ID", example = "123e4567-e89b-12d3-a456-426614174000")
    private String interfaceId;
    @Schema(description = "인터페이스 이름", example = "my-interface")
    private String interfaceName;
    @Schema(description = "인터페이스 상태", example = "ACTIVE")
    private String status;
    @Schema(description = "내부 IP", example = "192.168.0.1")
    private String internalIp;
    @Schema(description = "외부 IP", example = "192.168.0.1")
    private String externalIp;
    @Schema(description = "인스턴스 정보")
    private Instance instance;
    private Network network;
    @Schema(description = "MAC 주소", example = "00:00:00:00:00:00")
    private String mac;

    @Builder
    @Setter
    @Getter
    @Schema(description = "인스턴스 정보")
    public static class Instance {
        @Schema(description = "인스턴스 ID", example = "123e4567-e89b-12d3-a456-426614174000")
        private String instanceId;
        @Schema(description = "인스턴스 이름", example = "my-instance")
        private String instanceName;
    }

    @Builder
    @Setter
    @Getter
    @Schema(description = "네트워크 정보")
    public static class Network {
        @Schema(description = "네트워크 ID", example = "123e4567-e89b-12d3-a456-426614174000")
        private String networkId;
        @Schema(description = "네트워크 이름", example = "my-network")
        private String networkName;
    }

}
