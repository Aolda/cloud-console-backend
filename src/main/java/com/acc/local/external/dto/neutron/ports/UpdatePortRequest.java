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
public class UpdatePortRequest {
    private Port port;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Port {
        private String name;
        private String description;
        @JsonProperty("admin_state_up")
        private Boolean adminStateUp;
        @JsonProperty("fixed_ips")
        private List<FixedIp> fixedIps;
        @JsonProperty("device_id")
        private String deviceId;
        @JsonProperty("device_owner")
        private String deviceOwner;
        @JsonProperty("qos_policy_id")
        private String qosPolicyId;
        @JsonProperty("security_groups")
        private List<String> securityGroups;
        private List<String> tags;
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
