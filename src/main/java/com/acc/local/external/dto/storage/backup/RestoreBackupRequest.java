package com.acc.local.external.dto.storage.backup;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RestoreBackupRequest {

    @JsonProperty("restore")
    private Restore restore;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Restore {
        private String name; // Optional
        @JsonProperty("volume_id")
        private String volumeId; // Optional
    }
}
