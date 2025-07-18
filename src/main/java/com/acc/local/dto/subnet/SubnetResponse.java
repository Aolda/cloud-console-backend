package com.acc.dto.subnet;

public record SubnetResponse(
    String id,
    String name,
    String network_id,
    String cidr,
    boolean enable_dhcp,
    String gateway_ip,
    int ip_version
) {}