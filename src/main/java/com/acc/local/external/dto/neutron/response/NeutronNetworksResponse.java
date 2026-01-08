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
public class NeutronNetworksResponse {

    private List<Network> networks;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Network {
        private String id;
        private String name;
        private String status;

        @JsonProperty("project_id")
        private String projectId;

        @JsonProperty("tenant_id")
        private String tenantId;

        private Boolean shared;

        @JsonProperty("admin_state_up")
        private Boolean adminStateUp;

        private String description;

        @JsonProperty("router:external")
        private Boolean routerExternal;

        private Integer mtu;

        @JsonProperty("port_security_enabled")
        private Boolean portSecurityEnabled;

        private List<String> subnets;

        @JsonProperty("availability_zones")
        private List<String> availabilityZones;

        @JsonProperty("availability_zone_hints")
        private List<String> availabilityZoneHints;

        @JsonProperty("created_at")
        private String createdAt;

        @JsonProperty("updated_at")
        private String updatedAt;

        private List<String> tags;

        @JsonProperty("provider:network_type")
        private String providerNetworkType;

        @JsonProperty("provider:physical_network")
        private String providerPhysicalNetwork;

        @JsonProperty("provider:segmentation_id")
        private Integer providerSegmentationId;
    }
}