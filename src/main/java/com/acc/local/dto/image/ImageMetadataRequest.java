package com.acc.local.dto.image;

import lombok.Builder;

@Builder
public record ImageMetadataRequest(
        String name,
        String diskFormat,
        String containerFormat,
        String architecture,
        Integer minDisk,
        Integer minRam
) {}