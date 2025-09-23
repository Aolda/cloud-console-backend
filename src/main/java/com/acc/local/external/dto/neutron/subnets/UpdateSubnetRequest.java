package com.acc.local.external.dto.neutron.subnets;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UpdateSubnetRequest {
    private Subnet subnet;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Subnet {
        private String name;
        private String description;
        @JsonProperty("gateway_ip")
        private String gatewayIp;
        @JsonProperty("allocation_pools")
        private List<AllocationPool> allocationPools;
        @JsonProperty("dns_nameservers")
        private List<String> dnsNameservers;
        @JsonProperty("host_routes")
        private List<HostRoute> hostRoutes;
        @JsonProperty("enable_dhcp")
        private Boolean enableDhcp;
        private List<String> tags;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class AllocationPool {
        private String start;
        private String end;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class HostRoute {
        private String destination;
        private String nexthop;
    }
}
