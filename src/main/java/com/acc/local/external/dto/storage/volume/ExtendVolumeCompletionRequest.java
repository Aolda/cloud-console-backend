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
public class ExtendVolumeCompletionRequest {

    @JsonProperty("os-extend_volume_completion")
    private ExtendVolumeCompletion extendVolumeCompletion;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ExtendVolumeCompletion {
        @JsonProperty("error")
        private Boolean error;
    }
}
