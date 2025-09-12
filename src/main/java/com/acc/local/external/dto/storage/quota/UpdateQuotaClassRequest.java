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
public class UpdateQuotaClassRequest {

    @JsonProperty("quota_class_set")
    private QuotaClassSet quotaClassSet;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class QuotaClassSet {
        private Integer volumes;
        @JsonProperty("volumes_{volume_type}")
        private Integer volumesByType;       // optional
        private Integer snapshots;
        @JsonProperty("snapshots_{volume_type}")
        private Integer snapshotsByType;     // optional
        private Integer gigabytes;
        @JsonProperty("gigabytes_{volume_type}")
        private Integer gigabytesByType;     // optional
    }
}
