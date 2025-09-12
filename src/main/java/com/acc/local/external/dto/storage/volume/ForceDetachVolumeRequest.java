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
public class ForceDetachVolumeRequest {

    @JsonProperty("os-force_detach")
    private OsForceDetach osForceDetach;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class OsForceDetach {
        @JsonProperty("attachment_id")
        private String attachmentId; // Optional

        @JsonProperty("connector")
        private Connector connector; // Optional
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Connector {
        @JsonProperty("initiator")
        private String initiator;
    }
}
