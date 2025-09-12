package com.acc.local.external.dto.storage.quota;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateQuotaRequest {

    @JsonProperty("quota_set")
    private QuotaSet quotaSet;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class QuotaSet {
        private Integer volumes;
        @JsonProperty("volumes_{volume_type}")
        private Integer volumesByType;       // optional
        private Integer snapshots;
        @JsonProperty("snapshots_{volume_type}")
        private Integer snapshotsByType;     // optional
        private Integer backups;
        private Integer groups;
        @JsonProperty("per_volume_gigabytes")
        private Integer perVolumeGigabytes;  // optional
        private Integer gigabytes;
        @JsonProperty("gigabytes_{volume_type}")
        private Integer gigabytesByType;     // optional
        @JsonProperty("backup_gigabytes")
        private Integer backupGigabytes;     // optional
    }
}
