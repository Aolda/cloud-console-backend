package com.acc.local.external.dto.neutron.routers;

import com.acc.local.external.dto.neutron.common.UpdatableExternalGatewayInfo;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UpdateExternalGatewaysRequest {
    private Router router;
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Router {
        @JsonProperty("external_gateway_info")
        private UpdatableExternalGatewayInfo externalGatewayInfo;
    }
}
