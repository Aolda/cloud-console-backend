package com.acc.server.openstack.domain.model;

public record Network(
        String id,
        String name,
        String status
) {}
