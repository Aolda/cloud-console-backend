package com.acc.local.external.dto.storage.volume;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateVolumeRequest {

    private Volume volume;

    @JsonProperty("OS-SCH-HNT:scheduler_hints")
    private SchedulerHints schedulerHints;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Volume {
        private Integer size;

        @JsonProperty("availability_zone")
        private String availabilityZone;

        @JsonProperty("source_volid")
        private String sourceVolId;

        private String description;
        private Boolean multiattach;

        @JsonProperty("snapshot_id")
        private String snapshotId;

        @JsonProperty("backup_id")
        private String backupId;

        private String name;
        private String imageRef;

        @JsonProperty("volume_type")
        private String volumeType;

        private Map<String, String> metadata;

        @JsonProperty("consistencygroup_id")
        private String consistencygroupId;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class SchedulerHints {
        @JsonProperty("same_host")
        private List<String> sameHost;
    }
}
