package com.acc.local.external.dto.cinder.volume;

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
public class MigrateVolumeRequest {

    @JsonProperty("os-migrate_volume")
    private OsMigrateVolume osMigrateVolume;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @JsonInclude(JsonInclude.Include.NON_NULL)
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
