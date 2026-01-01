package com.acc.local.dto.network;

import com.acc.global.common.PageResponse;
import com.acc.local.domain.enums.network.ProtocolType;
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
    @Schema(description = "보안 그룹 이름",
            example = "my-security-group",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private String securityGroupName;

    @Schema(description = "보안 그룹 ID",
            example = "123e4567-e89b-12d3-a456-426614174000",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private String securityGroupId;

    @Schema(description = "보안 그룹 설명",
            example = "This is my security group",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String description;

    @Schema(description = """
            생성일시
            
            - ISO 8601 형식
            """,
            example = "2021-01-01T00:00:00Z",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private String createdAt;

    @Schema(description = "규칙 목록",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private PageResponse<Rule> rules;


    @Builder
    @Setter
    @Getter
    @Schema(description = "규칙 정보")
    public static class Rule {
        @Schema(description = "규칙 ID",
                example = "123e4567-e89b-12d3-a456-426614174000",
                requiredMode = Schema.RequiredMode.REQUIRED)
        private String ruleId;
        @Schema(description = """
                방향
                
                - ingress
                - egress
                """,
                example = "ingress",
                requiredMode = Schema.RequiredMode.REQUIRED)
        private String direction;

        @Schema(description = "포트 범위",
                example = "80:80",
                requiredMode = Schema.RequiredMode.REQUIRED)
        private String portRange;

        @Schema(description = "보안 그룹 ID",
                example = "123e4567-e89b-12d3-a456-426614174000",
                requiredMode = Schema.RequiredMode.NOT_REQUIRED)
        @JsonInclude(JsonInclude.Include.ALWAYS)
        private String remoteGroupId;

        @Schema(description = "CIDR",
                example = "192.168.0.0/24",
                requiredMode = Schema.RequiredMode.NOT_REQUIRED)
        @JsonInclude(JsonInclude.Include.ALWAYS)
        private String prefix;

        @Schema(description = """
                프로토콜
                
                - tcp
                - udp
                - icmp
                - ah
                - dccp
                - egp
                - esp
                - gre
                - icmpv6
                - igmp
                - ipip
                - ipv6-encap
                - ipv6-frag
                - ipv6-icmp
                - ipv6-nonxt
                - ipv6-opts
                - ipv6-route
                - ospf
                - pgm
                - rsvp
                - sctp
                - any
                """,
                example = "tcp",
                requiredMode = Schema.RequiredMode.REQUIRED)
        private ProtocolType protocol;
    }
}
