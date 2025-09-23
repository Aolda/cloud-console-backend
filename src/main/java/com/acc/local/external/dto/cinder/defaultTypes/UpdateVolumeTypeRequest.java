package com.acc.local.external.dto.cinder.defaultTypes;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
    public class UpdateVolumeTypeRequest {

    @JsonProperty("default_type")
    private DefaultType defaultType;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class DefaultType {
        @JsonProperty("volume_type")
        private String volumeType;
    }
}
