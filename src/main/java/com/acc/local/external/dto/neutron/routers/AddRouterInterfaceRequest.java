package com.acc.local.external.dto.neutron.routers;

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
public class AddRouterInterfaceRequest {
    @JsonProperty("subnet_id")
    private String subnetId;
    @JsonProperty("port_id")
    private String portId;
}
