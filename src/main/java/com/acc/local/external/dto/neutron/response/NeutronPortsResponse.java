package com.acc.local.external.dto.neutron.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NeutronPortsResponse {

    private List<Port> ports;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Port {
        private String id;
        private String name;
        private String status;

        @JsonProperty("network_id")
        private String networkId;

        @JsonProperty("project_id")
        private String projectId;

        @JsonProperty("tenant_id")
        private String tenantId;

        @JsonProperty("admin_state_up")
        private Boolean adminStateUp;

        @JsonProperty("mac_address")
        private String macAddress;

        @JsonProperty("fixed_ips")
        private List<FixedIp> fixedIps;

        @JsonProperty("device_id")
        private String deviceId;

        @JsonProperty("device_owner")
        private String deviceOwner;

        @JsonProperty("security_groups")
        private List<String> securityGroups;

        @JsonProperty("port_security_enabled")
        private Boolean portSecurityEnabled;

        private String description;

        @JsonProperty("binding:vnic_type")
        private String bindingVnicType;

        @JsonProperty("binding:host_id")
        private String bindingHostId;

        @JsonProperty("created_at")
        private String createdAt;

        @JsonProperty("updated_at")
        private String updatedAt;

        private List<String> tags;

        @JsonProperty("allowed_address_pairs")
        private List<AddressPair> allowedAddressPairs;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FixedIp {
        @JsonProperty("ip_address")
        private String ipAddress;

        @JsonProperty("subnet_id")
        private String subnetId;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AddressPair {
        @JsonProperty("ip_address")
        private String ipAddress;

        @JsonProperty("mac_address")
        private String macAddress;
    }
}