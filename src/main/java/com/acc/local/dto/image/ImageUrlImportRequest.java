package com.acc.local.dto.image;

import lombok.Builder;

@Builder
public record ImageUrlImportRequest(
        String name,
        String fileUrl,
        String diskFormat,
        String containerFormat,
        Integer minDisk,
        Integer minRam,
        String osVersion,
        String osAdminUser,
        String description
) {}

