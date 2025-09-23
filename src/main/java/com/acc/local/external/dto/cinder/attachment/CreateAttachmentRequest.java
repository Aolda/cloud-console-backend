package com.acc.local.external.dto.cinder.attachment;

import com.acc.local.external.dto.cinder.common.ConnectorDto;
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
public class CreateAttachmentRequest {

    @JsonProperty("attachment")
    private Attachment attachment;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Attachment {
        @JsonProperty("instance_uuid")
        private String instanceUuid; // Optional

        private ConnectorDto connector; // Optional

        @JsonProperty("volume_uuid")
        private String volumeUuid;

        private String mode; // Optional, "ro" or "rw"
    }
}
