package com.acc.local.external.dto.neutron.routers;

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
public class UpdateRouterRequest {
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
        @JsonProperty("external_gateway_info")
        private ExternalGatewayInfo externalGatewayInfo;
        private List<Route> routes;
        private List<String> tags;
    }

    @Data
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class ExternalGatewayInfo {
        // This can be complex, simplified for now based on common usage.
    }

    @Data
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Route {
        // This can be complex, simplified for now based on common usage.
    }
}
