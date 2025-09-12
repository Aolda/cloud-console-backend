package com.acc.local.external.dto.storage.volume;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReimageVolumeRequest {

    @JsonProperty("os-reimage")
    private OsReimage osReimage;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OsReimage {
        @JsonProperty("image_id")
        private String imageId;

        @JsonProperty("reimage_reserved")
        private Boolean reimageReserved; // Optional
    }
}
