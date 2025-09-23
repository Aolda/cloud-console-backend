package com.acc.local.external.dto.cinder.snapshot;

import com.fasterxml.jackson.annotation.JsonInclude;
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
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CreateSnapshotRequest {

    @JsonProperty("snapshot")
    private Snapshot snapshot;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Snapshot {
        private String name;
        private String description; // Optional
        @JsonProperty("volume_id")
        private String volumeId;
        private Boolean force; // Optional
        private Map<String, String> metadata; // Optional
    }
}
