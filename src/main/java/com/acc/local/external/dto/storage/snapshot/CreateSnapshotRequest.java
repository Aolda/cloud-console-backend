package com.acc.local.external.dto.storage.snapshot;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateSnapshotRequest {

    @JsonProperty("snapshot")
    private Snapshot snapshot;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Snapshot {
        private String name;
        private String description; // Optional
        @JsonProperty("volume_id")
        private String volumeId;
        private Boolean force; // Optional
        private Map<String, String> metadata; // Optional
    }
}
