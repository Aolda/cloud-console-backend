package com.acc.local.dto.network;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

import java.util.List;

@Getter
public class CreateNetworkRequest {

    @Schema(description = "네트워크 이름", example = "my-network")
    String networkName;

    @Schema(description = "네트워크 설명", example = "This is my network")
    String description;

    @Schema(description = "MTU 값", example = "1450")
    Integer mtu;

    @Schema(description = "서브넷 목록")
    List<Subnet> subnets;

    @Getter
    public static class Subnet {

        @Schema(description = "서브넷 CIDR", example = "192.168.0.0/24")
        String cidr;

        @Schema(description = "서브넷 이름", example = "my-subnet")
        String subnetName;
    }
}
