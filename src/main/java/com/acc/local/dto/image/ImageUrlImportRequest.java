package com.acc.local.dto.image;

import lombok.Builder;

@Builder
public record ImageUrlImportRequest(
        ImageMetadataRequest metadata,
        String fileUrl
) {}

