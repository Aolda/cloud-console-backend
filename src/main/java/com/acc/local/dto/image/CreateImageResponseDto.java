package com.acc.local.dto.image;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public record CreateImageResponseDto(
        String id,
        String name,
        String status,

        @JsonProperty("disk_format")
        String diskFormat,

        @JsonProperty("container_format")
        String containerFormat,

        String visibility,

        @JsonProperty("protected")
        boolean isProtected,

        @JsonProperty("min_ram")
        int minRam,

        @JsonProperty("min_disk")
        int minDisk,

        long size,

        @JsonProperty("virtual_size")
        long virtualSize,

        String checksum,

        String owner,

        @JsonProperty("created_at")
        String createdAt,

        @JsonProperty("updated_at")
        String updatedAt,

        @JsonProperty("os_hidden")
        boolean osHidden,

        @JsonProperty("os_hash_algo")
        String osHashAlgo,

        @JsonProperty("os_hash_value")
        String osHashValue
) {}
