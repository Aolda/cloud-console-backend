package com.acc.local.external.dto.storage.volume;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResetVolumeStatusRequest {

    @JsonProperty("os-reset_status")
    private ResetStatus resetStatus;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ResetStatus {
        @JsonProperty("status")
        private String status;

        @JsonProperty("attach_status")
        private String attachStatus;

        @JsonProperty("migration_status")
        private String migrationStatus;
    }
}
