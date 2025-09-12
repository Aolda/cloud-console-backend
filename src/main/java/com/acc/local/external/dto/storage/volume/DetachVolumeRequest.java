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
public class DetachVolumeRequest {

    @JsonProperty("os-detach")
    private OsDetach osDetach;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class OsDetach {
        @JsonProperty("attachment_id")
        private String attachmentId; // Optional
    }
}
