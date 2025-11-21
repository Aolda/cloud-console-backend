package com.acc.local.dto.network;

import com.acc.global.common.PageResponse;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Setter
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ViewSecurityGroupsResponse {
    private String securityGroupName;
    private String securityGroupId;
    private String description;
    private String createdAt;
    private PageResponse<Rule> rules;


    @Builder
    @Setter
    @Getter
    public static class Rule {
        private String ruleId;
        private String direction;
        private String protocol;
        private String portRange;
        @JsonInclude(JsonInclude.Include.ALWAYS)
        private String groupId;
        private String prefix;
    }
}
