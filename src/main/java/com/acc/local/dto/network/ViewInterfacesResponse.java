package com.acc.local.dto.network;

import com.acc.local.domain.enums.network.InterfaceStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Setter
@Getter
@Schema(description = "인터페이스 정보")
public class ViewInterfacesResponse {

    @Schema(description = "인터페이스 ID",
            example = "123e4567-e89b-12d3-a456-426614174000",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private String interfaceId;

    @Schema(description = "인터페이스 이름",
            example = "my-interface",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private String interfaceName;

    @Schema(description = """
            인터페이스 상태
            
            - ACTIVE
            - DOWN
            - BUILD
            - ERROR
            - UNKNOWN
            """,
            example = "ACTIVE",
        requiredMode = Schema.RequiredMode.REQUIRED)
    private InterfaceStatus status;

    @Schema(description = "Internal IP",
            example = "192.168.0.1",
        requiredMode = Schema.RequiredMode.REQUIRED)
    private String internalIp;

    @Schema(description = "External IP",
            example = "192.168.0.1",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String externalIp;

    @Schema(description = "인스턴스 정보",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Instance instance;

    @Schema(description = "네트워크 정보",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private Network network;

    @Schema(description = "MAC 주소",
            example = "00:00:00:00:00:00",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private String mac;

    @Builder
    @Setter
    @Getter
    @Schema(description = "인스턴스 정보")
    public static class Instance {
        @Schema(description = "인스턴스 ID",
                example = "123e4567-e89b-12d3-a456-426614174000",
                requiredMode = Schema.RequiredMode.REQUIRED)
        private String instanceId;
        @Schema(description = "인스턴스 이름",
                example = "my-instance",
                requiredMode = Schema.RequiredMode.REQUIRED)
        private String instanceName;
    }

    @Builder
    @Setter
    @Getter
    @Schema(description = "네트워크 정보")
    public static class Network {
        @Schema(description = "네트워크 ID",
                example = "123e4567-e89b-12d3-a456-426614174000",
                requiredMode = Schema.RequiredMode.REQUIRED)
        private String networkId;

        @Schema(description = "네트워크 이름",
                example = "my-network",
                requiredMode = Schema.RequiredMode.REQUIRED)
        private String networkName;
    }

}
