package com.acc.local.external.dto.cinder.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CinderVolumesResponse {

    private List<Volume> volumes;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Volume {
        private String id;
        private String name;
        private Integer size;
        private String status;

        @JsonProperty("volume_type")
        private String volumeType;

        private String description;

        @JsonProperty("availability_zone")
        private String availabilityZone;

        @JsonProperty("created_at")
        private String createdAt;

        @JsonProperty("updated_at")
        private String updatedAt;

        private String bootable;

        private List<Attachment> attachments;

        private Map<String, String> metadata;

        @JsonProperty("snapshot_id")
        private String snapshotId;

        @JsonProperty("source_volid")
        private String sourceVolid;

        private Boolean multiattach;

        @JsonProperty("volume_image_metadata")
        private Map<String, String> volumeImageMetadata;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Attachment {
        @JsonProperty("server_id")
        private String serverId;

        @JsonProperty("attachment_id")
        private String attachmentId;

        @JsonProperty("attached_at")
        private String attachedAt;

        @JsonProperty("host_name")
        private String hostName;

        private String device;
        private String id;
    }
}