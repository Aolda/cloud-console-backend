package com.acc.local.external.dto.neutron.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ExternalGatewayInfo {
    @JsonProperty("network_id")
    private String networkId;
    @JsonProperty("enable_snat")
    private Boolean enableSnat;
}
