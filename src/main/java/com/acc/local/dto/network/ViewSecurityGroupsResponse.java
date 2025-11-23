package com.acc.local.dto.network;

import com.acc.global.common.PageResponse;
import com.fasterxml.jackson.annotation.JsonInclude;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Setter
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "보안 그룹 정보", name = "ViewSecurityGroupsResponse")
public class ViewSecurityGroupsResponse {
    @Schema(description = "보안 그룹 이름", example = "my-security-group")
    private String securityGroupName;
    @Schema(description = "보안 그룹 ID", example = "123e4567-e89b-12d3-a456-426614174000")
    private String securityGroupId;
    @Schema(description = "보안 그룹 설명", example = "This is my security group")
    private String description;
    @Schema(description = "생성일시", example = "2021-01-01T00:00:00Z")
    private String createdAt;
    @Schema(description = "규칙 목록")
    private PageResponse<Rule> rules;


    @Builder
    @Setter
    @Getter
    @Schema(description = "규칙 정보")
    public static class Rule {
        @Schema(description = "규칙 ID", example = "123e4567-e89b-12d3-a456-426614174000")
        private String ruleId;
        @Schema(description = "방향", example = "ingress")
        private String direction;
        @Schema(description = "프로토콜", example = "tcp")
        private String protocol;
        @Schema(description = "포트 범위", example = "80:80")
        private String portRange;
        @Schema(description = "보안 그룹 ID", example = "123e4567-e89b-12d3-a456-426614174000")
        @JsonInclude(JsonInclude.Include.ALWAYS)
        private String groupId;
        @Schema(description = "CIDR", example = "192.168.0.0/24")
        private String prefix;
    }
}
