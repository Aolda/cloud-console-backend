package com.acc.local.external.dto.cinder.volume;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SetImageMetadataRequest {

    @JsonProperty("os-set_image_metadata")
    private ImageMetadata imageMetadata;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class ImageMetadata {
        @JsonProperty("metadata")
        private Map<String, String> metadata;
    }
}
