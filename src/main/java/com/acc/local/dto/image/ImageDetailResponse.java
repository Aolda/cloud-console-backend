package com.acc.local.dto.image;

import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

@Builder
public record ImageDetailResponse(
        String id,
        String name,
        String projectName,
        String description,     // properties.description (없으면 null)
        String diskFormat,
        String containerFormat,
        String visibility,
        String status,
        Long size,
        Long virtualSize,
        Integer minDisk,
        Integer minRam,
        String checksum,
        String osHashAlgo,
        String osHashValue,
        String osVersion,
        String osAdminUser,
        Boolean deleteProtected,
        Boolean hidden,
        List<String> tags,
        String stores,
        String createdAt,
        String updatedAt
) {}
