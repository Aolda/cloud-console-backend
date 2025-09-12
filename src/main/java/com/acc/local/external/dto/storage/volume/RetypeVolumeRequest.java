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
public class RetypeVolumeRequest {

    @JsonProperty("os-retype")
    private OsRetype osRetype;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class OsRetype {
        @JsonProperty("new_type")
        private String newType;

        @JsonProperty("migration_policy")
        private String migrationPolicy;
    }
}
