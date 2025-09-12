package com.acc.local.external.dto.storage.snapshot;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResetSnapshotStatusRequest {

    @JsonProperty("os-reset_status")
    private OsResetStatus osResetStatus;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class OsResetStatus {
        private String status;
    }
}
