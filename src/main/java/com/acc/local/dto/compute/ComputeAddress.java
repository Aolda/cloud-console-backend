package com.acc.local.dto.compute;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ComputeAddress(
        String addr,
        int version,
        @JsonProperty("OS-EXT-IPS:type") String type
) {}
