package com.acc.local.dto.flavor;
public record FlavorDto(
        String id,                     // ID
        String name,                   // Name
        Integer vcpus,                 // CPU (코어 수)
        Integer ram,                   // Memory (MB)
        Integer disk,                  // Root Disk (GiB)
        Integer ephemeral,             // Ephemeral Disk (GiB)
        Double rxtxFactor,             // Internal Network Bandwidth (상대값)
        Boolean isPublic           // Public 여부
) {}
