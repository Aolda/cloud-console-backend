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
public class NeutronSecurityGroupRuleResponse {
    @JsonProperty("security_group_rule")
    private SecurityGroupRule securityGroupRule;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class SecurityGroupRule {
        private String id;
        private String direction;
        private String protocol;

        @JsonProperty("port_range_min")
        private Integer portRangeMin;

        @JsonProperty("port_range_max")
        private Integer portRangeMax;

        @JsonProperty("remote_ip_prefix")
        private String remoteIpPrefix;

        @JsonProperty("remote_group_id")
        private String remoteGroupId;

        @JsonProperty("security_group_id")
        private String securityGroupId;

        @JsonProperty("tenant_id")
        private String tenantId;

        @JsonProperty("project_id")
        private String projectId;
    }
}