package com.acc.local.external.dto.neutron.routers;

import com.acc.local.external.dto.neutron.common.ExternalGatewayInfo;
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
public class CreateRouterRequest {

    private Router router;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Router {
        private String name;
        private String description;
        @JsonProperty("admin_state_up")
        private Boolean adminStateUp;
        @JsonProperty("project_id")
        private String projectId;
        private Boolean distributed;
        private Boolean ha;
        @JsonProperty("availability_zone_hints")
        private List<String> availabilityZoneHints;
        @JsonProperty("external_gateway_info")
        private ExternalGatewayInfo externalGatewayInfo;
        private List<String> tags;
    }
}
