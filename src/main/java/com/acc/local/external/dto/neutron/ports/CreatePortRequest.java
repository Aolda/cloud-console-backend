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
public class CreatePortRequest {

    private Port port;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Port {
        @JsonProperty("network_id")
        private String networkId;
        private String name;
        private String description;
        @JsonProperty("admin_state_up")
        private Boolean adminStateUp;
        @JsonProperty("mac_address")
        private String macAddress;
        @JsonProperty("fixed_ips")
        private List<FixedIp> fixedIps;
        @JsonProperty("project_id")
        private String projectId;
        @JsonProperty("device_id")
        private String deviceId;
        @JsonProperty("device_owner")
        private String deviceOwner;
        @JsonProperty("qos_policy_id")
        private String qosPolicyId;
        @JsonProperty("security_groups")
        private List<String> securityGroups;
        @JsonProperty("allowed_address_pairs")
        private List<AllowedAddressPair> allowedAddressPairs;
        @JsonProperty("extra_dhcp_opts")
        private List<ExtraDhcpOpt> extraDhcpOpts;
        @JsonProperty("binding:vnic_type")
        private String bindingVnicType;
        @JsonProperty("binding:host_id")
        private String bindingHostId;
        @JsonProperty("dns_name")
        private String dnsName;
        private List<String> tags;
        @JsonProperty("port_security_enabled")
        private Boolean portSecurityEnabled;
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

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class AllowedAddressPair {
        @JsonProperty("ip_address")
        private String ipAddress;
        @JsonProperty("mac_address")
        private String macAddress;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class ExtraDhcpOpt {
        @JsonProperty("opt_name")
        private String optName;
        @JsonProperty("opt_value")
        private String optValue;
        @JsonProperty("ip_version")
        private Integer ipVersion;
    }
}
