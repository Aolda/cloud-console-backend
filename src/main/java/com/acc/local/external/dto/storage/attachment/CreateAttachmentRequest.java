package com.acc.local.external.dto.storage.attachment;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateAttachmentRequest {

    @JsonProperty("attachment")
    private Attachment attachment;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Attachment {
        @JsonProperty("instance_uuid")
        private String instanceUuid; // Optional

        private Connector connector; // Optional

        @JsonProperty("volume_uuid")
        private String volumeUuid;

        private String mode; // Optional, "ro" or "rw"

        @Data
        @NoArgsConstructor
        @AllArgsConstructor
        @Builder
        public static class Connector {
            private String initiator;
            private String ip;
            private String platform;
            private String host;
            @JsonProperty("os_type")
            private String osType;
            private Boolean multipath;
            private String mountpoint;
            private String mode; // Optional
        }
    }
}
