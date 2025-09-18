package com.acc.local.external.dto.cinder.snapshot;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)

public class UpdateSnapshotRequest {

    @JsonProperty("snapshot")
    private Snapshot snapshot;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @JsonInclude(JsonInclude.Include.NON_NULL)

    public static class Snapshot {
        private String name; // Optional
        private String description; // Optional
    }
}
