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
public class UpdateSecurityGroupRequest {
    @JsonProperty("security_group")
    private SecurityGroup securityGroup;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class SecurityGroup {
        private String name;
        private String description;
        private Boolean stateful;
        private List<String> tags;
    }
}
