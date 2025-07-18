package com.acc.dto.port;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PortResponse {
    private String id;
    private String name;
    private String deviceId;
    private String deviceOwner;
    private String macAddress;
    private String status;
    private String networkId;
    private String fixedIp;
    private String deviceName; // 연결 대상 이름 (인스턴스/라우터)
}
