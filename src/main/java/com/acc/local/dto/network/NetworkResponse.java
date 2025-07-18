package com.acc.dto.network;

public record NetworkResponse(
    String id,
    String name,
    boolean admin_state_up,
    boolean shared
) {}