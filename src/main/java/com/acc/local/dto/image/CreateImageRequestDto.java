package com.acc.local.dto.image;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.HashMap;
import java.util.Map;

public record CreateImageRequestDto(
        String name,

        @JsonProperty("disk_format")
        String diskFormat,

        @JsonProperty("container_format")
        String containerFormat,

        String visibility,

        @JsonProperty("min_disk")
        int minDisk,

        @JsonProperty("min_ram")
        int minRam,

        boolean isProtected
) {public Map<String, Object> toMap() {
    Map<String, Object> map = new HashMap<>();
    map.put("name", name);
    map.put("disk_format", diskFormat);
    map.put("container_format", containerFormat);
    map.put("visibility", visibility);
    map.put("min_disk", minDisk);
    map.put("min_ram", minRam);
    map.put("protected", isProtected);
    return map;
}
}

