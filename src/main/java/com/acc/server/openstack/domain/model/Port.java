package com.acc.server.openstack.domain.model;

public record Port(
        String id,
        String name,
        String networkId,
        String macAddress
) {}
