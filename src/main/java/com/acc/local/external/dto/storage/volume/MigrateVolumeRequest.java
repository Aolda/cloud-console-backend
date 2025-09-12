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
public class MigrateVolumeRequest {

    @JsonProperty("os-migrate_volume")
    private OsMigrateVolume osMigrateVolume;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class OsMigrateVolume {
        @JsonProperty("host")
        private String host; // Optional

        @JsonProperty("force_host_copy")
        private Boolean forceHostCopy; // Optional

        @JsonProperty("lock_volume")
        private Boolean lockVolume; // Optional

        @JsonProperty("cluster")
        private String cluster; // Optional
    }
}
