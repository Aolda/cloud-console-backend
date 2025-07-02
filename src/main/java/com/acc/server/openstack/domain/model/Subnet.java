package com.acc.server.openstack.domain.model;

public record Subnet(
        String id,
        String name,
        String networkId,
        String cidr
) {}
