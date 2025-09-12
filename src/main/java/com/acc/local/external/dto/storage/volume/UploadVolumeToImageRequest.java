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
public class UploadVolumeToImageRequest {

    @JsonProperty("os-volume_upload_image")
    private OsVolumeUploadImage osVolumeUploadImage;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class OsVolumeUploadImage {
        @JsonProperty("image_name")
        private String imageName;

        @JsonProperty("force")
        private Boolean force; // Optional

        @JsonProperty("disk_format")
        private String diskFormat; // Optional

        @JsonProperty("container_format")
        private String containerFormat; // Optional

        @JsonProperty("visibility")
        private String visibility; // Optional

        @JsonProperty("protected")
        private Boolean protectedFlag; // Optional
    }
}
