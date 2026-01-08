package com.acc.local.external.dto.neutron.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NeutronFloatingIpsResponse {

    private List<FloatingIp> floatingips;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FloatingIp {
        private String id;

        @JsonProperty("floating_ip_address")
        private String floatingIpAddress;

        @JsonProperty("floating_network_id")
        private String floatingNetworkId;

        @JsonProperty("router_id")
        private String routerId;

        @JsonProperty("port_id")
        private String portId;

        @JsonProperty("fixed_ip_address")
        private String fixedIpAddress;

        @JsonProperty("project_id")
        private String projectId;

        @JsonProperty("tenant_id")
        private String tenantId;

        private String status;
        private String description;

        @JsonProperty("created_at")
        private String createdAt;

        @JsonProperty("updated_at")
        private String updatedAt;

        private List<String> tags;
    }
}