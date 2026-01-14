package com.acc.local.external.dto.glance.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class GlanceImagesResponse {
    private List<Image> images;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Image {
        private String id;
        private String name;
        private String status;
        private String visibility;
        private Long size;

        @JsonProperty("owner")
        private String owner;

        @JsonProperty("checksum")
        private String checksum;

        @JsonProperty("container_format")
        private String containerFormat;

        @JsonProperty("disk_format")
        private String diskFormat;

        @JsonProperty("created_at")
        private String createdAt;

        @JsonProperty("updated_at")
        private String updatedAt;

        private List<String> tags;
    }
}

