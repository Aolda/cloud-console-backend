package com.acc.local.external.dto.cinder.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CinderQuotaResponse {

    @JsonProperty("quota_set")
    private QuotaSet quotaSet;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class QuotaSet {
        private Integer volumes;
        private Integer snapshots;
        private Integer gigabytes;

        @JsonProperty("per_volume_gigabytes")
        private Integer perVolumeGigabytes;

        private Integer backups;

        @JsonProperty("backup_gigabytes")
        private Integer backupGigabytes;

        private Integer groups;

        // Usage information (when detail=true)
        @JsonProperty("in_use")
        private InUse inUse;

        private Integer reserved;
        private Integer limit;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class InUse {
        private Integer volumes;
        private Integer snapshots;
        private Integer gigabytes;
        private Integer backups;

        @JsonProperty("backup_gigabytes")
        private Integer backupGigabytes;

        private Integer groups;
    }
}