package com.acc.dto.network;

public record NetworkUpdateRequest(
    String name,
    Boolean admin_state_up,
    Boolean shared
) {}