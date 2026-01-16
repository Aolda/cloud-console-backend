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
@Schema(description = "인터페이스 연결 요청")
public class InterfaceAttachmentRequest {

    @Schema(description = "인터페이스 ID (interfaceId와 networkId는 상호 배타적)", example = "ce531f90-199f-48c0-816c-13e38010b442")
    private String interfaceId;

    @Schema(description = "네트워크 ID (interfaceId와 networkId는 상호 배타적)", example = "3cb9bc59-5699-4588-a4b1-b87f96708bc6")
    private String networkId;

    @Schema(description = "고정 IP 주소 목록 (networkId와 함께 사용 가능)")
    private List<FixedIp> fixedIps;

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "고정 IP 정보")
    public static class FixedIp {
        @Schema(description = "IP 주소", example = "192.168.1.3", required = true)
        private String ipAddress;
    }
}

