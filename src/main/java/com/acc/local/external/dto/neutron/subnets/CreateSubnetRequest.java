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
public class CreateSubnetRequest {

    private Subnet subnet;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Subnet {
        @JsonProperty("network_id")
        private String networkId;
        private String name;
        private String description;
        @JsonProperty("ip_version")
        private Integer ipVersion;
        private String cidr;
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
        @JsonProperty("project_id")
        private String projectId;
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
