package com.acc.local.dto.instance;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "인터페이스 연결 정보")
public class InterfaceAttachmentResponse {

    @Schema(description = "인터페이스 ID", example = "ce531f90-199f-48c0-816c-13e38010b442")
    private String interfaceId;

    @Schema(description = "네트워크 ID", example = "3cb9bc59-5699-4588-a4b1-b87f96708bc6")
    private String networkId;

    @Schema(description = "MAC 주소", example = "fa:16:3e:4c:2c:30")
    private String macAddr;

    @Schema(description = "인터페이스 상태", example = "ACTIVE")
    private String interfaceState;

    @Schema(description = "고정 IP 주소 목록")
    private List<FixedIp> fixedIps;

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "고정 IP 정보")
    public static class FixedIp {
        @Schema(description = "IP 주소", example = "192.168.1.3")
        private String ipAddress;

        @Schema(description = "서브넷 ID", example = "f8a6e8f8-c2ec-497c-9f23-da9616de54ef")
        private String subnetId;
    }
}

