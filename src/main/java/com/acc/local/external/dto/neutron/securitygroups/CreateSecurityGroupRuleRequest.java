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
public class CreateSecurityGroupRuleRequest {

    @JsonProperty("security_group_rule")
    private SecurityGroupRule securityGroupRule;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class SecurityGroupRule {
        @JsonProperty("security_group_id")
        private String securityGroupId;
        private String direction;
        private String protocol;
        @JsonProperty("port_range_min")
        private Integer portRangeMin;
        @JsonProperty("port_range_max")
        private Integer portRangeMax;
        private String ethertype;
        @JsonProperty("remote_ip_prefix")
        private String remoteIpPrefix;
        @JsonProperty("remote_group_id")
        private String remoteGroupId;
        private String description;
        @JsonProperty("project_id")
        private String projectId;
    }
}
