package com.acc.local.external.dto.neutron.routers;

import com.acc.local.external.dto.neutron.common.RouterForExternalGateways;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AddExternalGatewaysRequest {
    private RouterForExternalGateways router;
}
