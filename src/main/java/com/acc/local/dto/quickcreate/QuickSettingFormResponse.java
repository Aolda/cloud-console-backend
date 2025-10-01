package com.acc.local.dto.quickcreate;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class QuickSettingFormResponse {

    @Schema(description = "사용 가능한 flavor 목록")
    List<Flavor> flavors;

    @Schema(description = "기본 네트워크 정보")
    Network network;

    @Getter
    @Setter
    @Builder
    static class Flavor {

        @Schema(description = "flavor ID", example = "fef0df7f-c3bd-4c36-941c-f476e9f155f3")
        String id;

        @Schema(description = "flavor 이름", example = "tb1.mini")
        String name;

        @Schema(description = "vCPU 개수", example = "1")
        int vcpus;

        @Schema(description = "RAM 크기 (GB)", example = "1")
        Double ram;
    }

    @Getter
    @Setter
    @Builder
    static class Network {

        @Schema(description = "네트워크 이름", example = "default-network")
        String name = "default-network";

        @Schema(description = "네트워크 서브넷 대역", example = "192.168.1.0/24")
        String subnet;
    }

}
