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
public class CompleteMigrateVolumeRequest {

    @JsonProperty("os-migrate_volume_completion")
    private OsMigrateVolumeCompletion osMigrateVolumeCompletion;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class OsMigrateVolumeCompletion {
        @JsonProperty("new_volume")
        private String newVolume;

        @JsonProperty("error")
        private Boolean error; // Optional
    }
}
