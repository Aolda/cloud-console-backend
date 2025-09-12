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
public class RevertVolumeRequest {

    @JsonProperty("revert")
    private Revert revert;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Revert {
        @JsonProperty("snapshot_id")
        private String snapshotId;
    }
}
