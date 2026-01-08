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
public class NeutronSecurityGroupRulesResponse {

    @JsonProperty("security_group_rules")
    private List<NeutronSecurityGroupsResponse.SecurityGroupRule> securityGroupRules;
}