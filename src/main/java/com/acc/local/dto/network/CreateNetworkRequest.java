package com.acc.local.dto.network;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
public class CreateNetworkRequest {

    @Schema(description = "네트워크 이름", example = "my-network")
    @NotBlank
    @Pattern(regexp = "^[a-zA-Z][0-9a-zA-Z\\-_()\\[\\]\\.:^]{0,127}$")
    String networkName;

    @Schema(description = "네트워크 설명", example = "This is my network")
    String description;

    @Schema(description = "MTU 값", example = "1450", defaultValue = "1450")
    @Min(value = 68)
    @Max(value = 65535)
    Integer mtu = 1450;

    @Schema(description = "서브넷 목록")
    List<Subnet> subnets;

    @Getter
    @Builder
    @Schema(description = "서브넷 정보")
    public static class Subnet {

        @Schema(description = "서브넷 CIDR", example = "192.168.0.0/24")
        @NotBlank
        @Pattern(regexp = "^((25[0-5]|2[0-4][0-9]|1[0-9]{2}|[1-9][0-9]|[0-9])\\." +
                                "){3}(25[0-5]|2[0-4][0-9]|1[0-9]{2}|[1-9][0-9]|[0-9])\\/" +
                                "(?:[1-9]|[12]\\d|3[0-2])$")
        String cidr;

        @Schema(description = "서브넷 이름", example = "my-subnet")
        @NotBlank
        @Pattern(regexp = "^[a-zA-Z][0-9a-zA-Z\\-_()\\[\\]\\.:^]{0,127}$")
        String subnetName;
    }
}
