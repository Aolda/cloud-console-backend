package com.acc.dto.network;

import jakarta.validation.constraints.NotBlank;

public record NetworkRequest(
    @NotBlank String name,
    boolean admin_state_up,
    boolean shared
) {}
