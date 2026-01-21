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
public class NeutronSecurityGroupsResponse {

    @JsonProperty("security_groups")
    private List<SecurityGroup> securityGroups;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SecurityGroup {
        private String id;
        private String name;
        private String description;

        @JsonProperty("project_id")
        private String projectId;

        @JsonProperty("tenant_id")
        private String tenantId;

        @JsonProperty("security_group_rules")
        private List<SecurityGroupRule> securityGroupRules;

        @JsonProperty("created_at")
        private String createdAt;

        @JsonProperty("updated_at")
        private String updatedAt;

        private List<String> tags;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SecurityGroupRule {
        private String id;
        private String direction;  // "ingress" or "egress"

        @JsonProperty("ethertype")
        private String ethertype;  // "IPv4" or "IPv6"

        @JsonProperty("port_range_min")
        private Integer portRangeMin;

        @JsonProperty("port_range_max")
        private Integer portRangeMax;

        private String protocol;  // "tcp", "udp", "icmp", null

        @JsonProperty("remote_group_id")
        private String remoteGroupId;

        @JsonProperty("remote_ip_prefix")
        private String remoteIpPrefix;

        @JsonProperty("security_group_id")
        private String securityGroupId;

        @JsonProperty("project_id")
        private String projectId;

        @JsonProperty("tenant_id")
        private String tenantId;

        @JsonProperty("created_at")
        private String createdAt;

        @JsonProperty("updated_at")
        private String updatedAt;

        private String description;
    }
}