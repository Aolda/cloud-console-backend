package com.acc.local.external.dto.storage.defaultTypes;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
    public class UpdateVolumeTypeRequest {

    @JsonProperty("default_type")
    private DefaultType defaultType;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class DefaultType {
        @JsonProperty("volume_type")
        private String volumeType;
    }
}
