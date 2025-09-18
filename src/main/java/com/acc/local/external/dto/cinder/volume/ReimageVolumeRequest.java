package com.acc.local.external.dto.cinder.volume;

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
public class ReimageVolumeRequest {

    @JsonProperty("os-reimage")
    private OsReimage osReimage;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class OsReimage {
        @JsonProperty("image_id")
        private String imageId;

        @JsonProperty("reimage_reserved")
        private Boolean reimageReserved; // Optional
    }
}
