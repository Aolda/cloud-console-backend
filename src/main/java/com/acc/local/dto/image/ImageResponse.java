package com.acc.local.dto.image;

public record ImageResponse(
        String id,
        String name,
        String status,
        String visibility,
        Long size,
        String diskFormat,
        String containerFormat,
        String createdAt,
        String updatedAt
){}
