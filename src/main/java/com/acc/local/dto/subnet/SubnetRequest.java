package com.acc.dto.subnet;

import jakarta.validation.constraints.NotBlank;

public record SubnetRequest(
    @NotBlank String name,
    @NotBlank String network_id,
    @NotBlank String cidr,
    String gateway_ip,
    boolean enable_dhcp,
    String ip_version // "ipv4" 또는 "ipv6"
) {}