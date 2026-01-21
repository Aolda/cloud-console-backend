package com.acc.local.external.dto.neutron.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NeutronSecurityGroupResponse {

    @JsonProperty("security_group")
    private NeutronSecurityGroupsResponse.SecurityGroup securityGroup;
}