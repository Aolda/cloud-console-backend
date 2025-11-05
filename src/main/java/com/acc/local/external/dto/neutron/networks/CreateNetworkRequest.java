package com.acc.local.external.dto.neutron.networks;

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
public class CreateNetworkRequest {
    private Network network;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Network {
        private String name;
        private String description;
        private Integer mtu;
        @JsonProperty("admin_state_up")
        private Boolean adminStateUp;
        private Boolean shared;
        @JsonProperty("tenant_id")
        private String tenantId;
        @JsonProperty("project_id")
        private String projectId;
        @JsonProperty("provider:network_type")
        private String providerNetworkType;
        @JsonProperty("provider:physical_network")
        private String providerPhysicalNetwork;
        @JsonProperty("provider:segmentation_id")
        private Integer providerSegmentationId;
        private List<Segment> segments;
        @JsonProperty("port_security_enabled")
        private Boolean portSecurityEnabled;
        @JsonProperty("qos_policy_id")
        private String qosPolicyId;
        @JsonProperty("vlan_transparent")
        private Boolean vlanTransparent;
        @JsonProperty("is_default")
        private Boolean isDefault;
        @JsonProperty("availability_zone_hints")
        private List<String> availabilityZoneHints;
        @JsonProperty("availability_zones")
        private List<String> availabilityZones;
        private List<String> tags;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Segment {
        @JsonProperty("provider:network_type")
        private String providerNetworkType;
        @JsonProperty("provider:physical_network")
        private String providerPhysicalNetwork;
        @JsonProperty("provider:segmentation_id")
        private Integer providerSegmentationId;
    }
}
