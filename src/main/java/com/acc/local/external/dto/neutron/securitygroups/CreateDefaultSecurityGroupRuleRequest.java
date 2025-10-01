package com.acc.local.external.dto.neutron.securitygroups;

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
public class CreateDefaultSecurityGroupRuleRequest {
    @JsonProperty("default_security_group_rule")
    private DefaultSecurityGroupRule defaultSecurityGroupRule;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class DefaultSecurityGroupRule {
        private String direction;
        private String ethertype;
        @JsonProperty("port_range_max")
        private Integer portRangeMax;
        @JsonProperty("port_range_min")
        private Integer portRangeMin;
        private String protocol;
        @JsonProperty("remote_group_id")
        private String remoteGroupId;
        @JsonProperty("remote_ip_prefix")
        private String remoteIpPrefix;
        @JsonProperty("project_id")
        private String projectId;
    }
}
