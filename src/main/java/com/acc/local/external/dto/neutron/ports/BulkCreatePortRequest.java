package com.acc.local.external.dto.neutron.ports;

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
public class BulkCreatePortRequest {
    private List<Port> ports;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Port {
        @JsonProperty("admin_state_up")
        private Boolean adminStateUp;
        @JsonProperty("device_id")
        private String deviceId;
        @JsonProperty("device_owner")
        private String deviceOwner;
        @JsonProperty("fixed_ips")
        private List<FixedIp> fixedIps;
        @JsonProperty("network_id")
        private String networkId;
        private String name;
        @JsonProperty("project_id")
        private String projectId;
        @JsonProperty("security_groups")
        private List<String> securityGroups;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class FixedIp {
        @JsonProperty("subnet_id")
        private String subnetId;
        @JsonProperty("ip_address")
        private String ipAddress;
    }
}
