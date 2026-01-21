package com.acc.local.external.dto.cinder.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CinderSnapshotsResponse {

    private List<Snapshot> snapshots;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Snapshot {
        private String id;
        private String name;
        private String description;
        private String status;
        private Integer size;

        @JsonProperty("volume_id")
        private String volumeId;

        @JsonProperty("created_at")
        private String createdAt;

        @JsonProperty("updated_at")
        private String updatedAt;

        private Map<String, String> metadata;
    }
}