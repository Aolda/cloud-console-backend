package com.acc.dto.subnet;

public record SubnetUpdateRequest(
    String name,
    Boolean enable_dhcp,
    String gateway_ip
) {}