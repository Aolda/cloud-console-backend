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
public class NeutronRoutersResponse {

    private List<Router> routers;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Router {
        private String id;
        private String name;
        private String status;

        @JsonProperty("project_id")
        private String projectId;

        @JsonProperty("tenant_id")
        private String tenantId;

        @JsonProperty("admin_state_up")
        private Boolean adminStateUp;

        private String description;

        @JsonProperty("external_gateway_info")
        private ExternalGatewayInfo externalGatewayInfo;

        @JsonProperty("created_at")
        private String createdAt;

        @JsonProperty("updated_at")
        private String updatedAt;

        private List<String> tags;

        @JsonProperty("availability_zones")
        private List<String> availabilityZones;

        @JsonProperty("availability_zone_hints")
        private List<String> availabilityZoneHints;

        private Boolean distributed;

        private Boolean ha;

        @JsonProperty("routes")
        private List<Route> routes;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ExternalGatewayInfo {
        @JsonProperty("network_id")
        private String networkId;

        @JsonProperty("enable_snat")
        private Boolean enableSnat;

        @JsonProperty("external_fixed_ips")
        private List<ExternalFixedIp> externalFixedIps;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ExternalFixedIp {
        @JsonProperty("ip_address")
        private String ipAddress;

        @JsonProperty("subnet_id")
        private String subnetId;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Route {
        private String destination;
        private String nexthop;
    }
}