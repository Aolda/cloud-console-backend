    package com.acc.local.dto.network;

    import com.fasterxml.jackson.annotation.JsonInclude;

    import io.swagger.v3.oas.annotations.media.Schema;
    import lombok.Builder;
    import lombok.Getter;
    import lombok.Setter;

    import java.util.List;

    @Builder
    @Setter
    @Getter
    @Schema(description = "네트워크 조회 응답")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public class ViewNetworksResponse {
        @Schema(description = "네트워크 ID", example = "123e4567-e89b-12d3-a456-426614174000")
        String networkId;
        @Schema(description = "네트워크 이름", example = "my-network")
        String networkName;
        @Schema(description = "서브넷 목록")
        List<Subnet> subnets;

        @Builder
        @Getter
        @Setter
        @Schema(description = "서브넷 정보")
        public static class Subnet {
            @Schema(description = "서브넷 ID", example = "123e4567-e89b-12d3-a456-426614174000")
            String subnetId;
            @Schema(description = "서브넷 이름", example = "my-subnet")
            String subnetName;
            @Schema(description = "서브넷 CIDR", example = "192.168.0.0/24")
            String cidr;
        }
    }
