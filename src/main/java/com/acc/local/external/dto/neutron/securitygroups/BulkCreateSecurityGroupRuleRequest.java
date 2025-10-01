package com.acc.local.external.dto.neutron.securitygroups;

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
public class BulkCreateSecurityGroupRuleRequest {
    @JsonProperty("security_group_rules")
    private List<SecurityGroupRule> securityGroupRules;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class SecurityGroupRule {
        private String direction;
        private String ethertype;
        @JsonProperty("port_range_max")
        private Integer portRangeMax;
        @JsonProperty("port_range_min")
        private Integer portRangeMin;
        private String protocol;
        @JsonProperty("remote_group_id")
        private String remoteGroupId;
        @JsonProperty("security_group_id")
        private String securityGroupId;
    }
}
