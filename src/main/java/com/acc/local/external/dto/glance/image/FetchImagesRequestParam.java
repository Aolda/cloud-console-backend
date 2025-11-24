package com.acc.local.external.dto.glance.image;

import lombok.*;

import java.util.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FetchImagesRequestParam {

    // Pagination
    private Integer limit;
    private String marker;

    // Direct filters
    private String name;
    private String visibility;   // public|private|shared|community|all
    private String status;       // active|queued|saving
    private String owner;
    private Boolean deleteProtected; // maps to "protected"
    private Boolean hidden;

    private List<String> tags;

    private String containerFormat;
    private String diskFormat;
    private String id;

    private Long sizeMin;
    private Long sizeMax;

    private String createdAt;
    private String updatedAt;

    private String sortKey;
    private String sortDir;
    private String sort;

    public Map<String, String> toQueryParams() {
        Map<String, String> map = new LinkedHashMap<>();
        if (limit != null) map.put("limit", String.valueOf(limit));
        if (marker != null) map.put("marker", marker);
        if (name != null) map.put("name", name);
        if (visibility != null) map.put("visibility", visibility);
        if (status != null) map.put("status", status);
        if (owner != null) map.put("owner", owner);
        if (deleteProtected != null) map.put("protected", String.valueOf(deleteProtected));
        if (hidden != null) map.put("hidden", String.valueOf(hidden));
        if (tags != null && !tags.isEmpty()) map.put("tag", String.join(",", tags));
        if (containerFormat != null) map.put("container_format", containerFormat);
        if (diskFormat != null) map.put("disk_format", diskFormat);
        if (id != null) map.put("id", id);
        if (sizeMin != null) map.put("size_min", String.valueOf(sizeMin));
        if (sizeMax != null) map.put("size_max", String.valueOf(sizeMax));
        if (createdAt != null) map.put("created_at", createdAt);
        if (updatedAt != null) map.put("updated_at", updatedAt);
        if (sortKey != null) map.put("sort_key", sortKey);
        if (sortDir != null) map.put("sort_dir", sortDir);
        if (sort != null) map.put("sort", sort);
        return map;
    }
}
