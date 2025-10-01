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
public class BulkCreateSubnetRequest {
    private List<Subnet> subnets;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Subnet {
        private String cidr;
        @JsonProperty("gateway_ip")
        private String gatewayIp;
        @JsonProperty("ip_version")
        private Integer ipVersion;
        @JsonProperty("network_id")
        private String networkId;
        private String name;
        @JsonProperty("project_id")
        private String projectId;
    }
}
