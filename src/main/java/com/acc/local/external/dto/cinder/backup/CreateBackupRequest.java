package com.acc.local.external.dto.cinder.backup;

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
public class CreateBackupRequest {

    @JsonProperty("backup")
    private Backup backup;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Backup {
        @JsonProperty("volume_id")
        private String volumeId;
        private String container; // Optional
        private String description; // Optional
        private Boolean incremental; // Optional
        private Boolean force; // Optional
        private String name; // Optional
        @JsonProperty("snapshot_id")
        private String snapshotId; // Optional
        private Map<String, String> metadata; // Optional
        @JsonProperty("availability_zone")
        private String availabilityZone; // Optional
    }
}
