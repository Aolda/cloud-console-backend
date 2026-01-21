package com.acc.local.external.dto.cinder.response;

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
public class CinderVolumesResponse {
    private List<Volume> volumes;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Volume {
        private String id;
        private String name;
        private String status;
        private Integer size;

        @JsonProperty("volume_type")
        private String volumeType;

        @JsonProperty("created_at")
        private String createdAt;

        private String description;

        @JsonProperty("availability_zone")
        private String availabilityZone;

        // Cinder often returns "bootable": "true" as string; Jackson will coerce to Boolean when possible.
        @JsonProperty("bootable")
        private Boolean bootable;
    }
}
