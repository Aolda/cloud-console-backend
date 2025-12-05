package com.acc.local.dto.network;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;

@Getter
public class CreateSecurityRuleRequest {

    @Schema(description = "프로토콜", example = "tcp")
    @NotBlank
    @Pattern(regexp = "^(tcp|udp|icmp|ah|dccp|egp|esp|gre|icmpv6|igmp|ipip|ipv6-encap|ipv6-frag|ipv6-icmp|ipv6-nonxt|ipv6-opts|ipv6-route|ospf|pgm|rsvp|sctp|any)$")
    private String protocol;

    @Schema(description = "방향", example = "ingress or egress")
    @NotBlank
    @Pattern(regexp = "^(ingress|egress)$")
    private String direction;

    @Schema(description = "포트", example = "80")
    @Min(value = 1)
    @Max(value = 65535)
    @NotNull
    private Integer port;

    @Schema(description = "허용 보안 그룹 ID", example = "sg-12345678")
    private String remoteSecurityGroupId;

    @Schema(description = "허용 CIDR", example = "192.168.0.0/24")
    @Pattern(regexp = "^((25[0-5]|2[0-4][0-9]|1[0-9]{2}|[1-9][0-9]|[0-9])\\." +
                                "){3}(25[0-5]|2[0-4][0-9]|1[0-9]{2}|[1-9][0-9]|[0-9])\\/" +
                                "(?:[1-9]|[12]\\d|3[0-2])$")
    private String cidr;
}
