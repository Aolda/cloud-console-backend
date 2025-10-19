package com.acc.local.external.dto.neutron.quotas;

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
public class UpdateQuotaRequest {

    @JsonProperty("quota")
    private Quota quota;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Quota {
        @JsonProperty("floatingip")
        private Integer floatingip;

        @JsonProperty("network")
        private Integer network;

        @JsonProperty("port")
        private Integer port;

        @JsonProperty("rbac_policy")
        private Integer rbacPolicy;

        @JsonProperty("router")
        private Integer router;

        @JsonProperty("security_group")
        private Integer securityGroup;

        @JsonProperty("security_group_rule")
        private Integer securityGroupRule;

        @JsonProperty("subnet")
        private Integer subnet;

        @JsonProperty("subnetpool")
        private Integer subnetpool;

        @JsonProperty("check_limit")
        private String checkLimit;

        @JsonProperty("force")
        private String force;
    }
}
