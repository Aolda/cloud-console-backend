package com.acc.local.dto.image;

import lombok.Builder;

import java.util.List;

@Builder
public record ImageListResponse(
        List<GlanceImageSummary> images
) {

    @Builder
    public record GlanceImageSummary(
            String id,
            String name,
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
}
