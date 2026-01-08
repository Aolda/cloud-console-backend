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
public class NeutronSubnetsResponse {

    private List<Subnet> subnets;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Subnet {
        private String id;
        private String name;
        private String cidr;

        @JsonProperty("network_id")
        private String networkId;

        @JsonProperty("project_id")
        private String projectId;

        @JsonProperty("tenant_id")
        private String tenantId;

        @JsonProperty("created_at")
        private String createdAt;

        @JsonProperty("updated_at")
        private String updatedAt;
    }
}

