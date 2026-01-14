package com.acc.local.external.dto.neutron.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class NeutronRouterInterfaceResponse {
    private String id;

    @JsonProperty("port_id")
    private String portId;

    @JsonProperty("subnet_id")
    private String subnetId;

    @JsonProperty("tenant_id")
    private String tenantId;

    @JsonProperty("project_id")
    private String projectId;
}