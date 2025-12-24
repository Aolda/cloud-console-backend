package com.acc.local.dto.image;

import lombok.Builder;

@Builder
public record GlanceImageSummary(
        String id,
        String name,
        String architecture,
        String projectName,
        String description,
        String diskFormat,
        String status,
        String visibility,
        Long size,
        Boolean hidden,
        Integer minDisk,
        Integer minRam,
        String createdAt
) {}
