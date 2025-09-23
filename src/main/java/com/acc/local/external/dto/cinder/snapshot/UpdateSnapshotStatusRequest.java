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

public class UpdateSnapshotStatusRequest {

    @JsonProperty("os-update_snapshot_status")
    private OsUpdateSnapshotStatus osUpdateSnapshotStatus;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @JsonInclude(JsonInclude.Include.NON_NULL)

    public static class OsUpdateSnapshotStatus {
        private String status;
        private String progress; // Optional
    }
}
