package com.acc.local.external.dto.nova.volumeAttachment;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UpdateVolumeAttachmentsRequest {
    private UpdateVolumeAttachment volumeAttachment;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class UpdateVolumeAttachment {
        private Boolean delete_on_termination;
        private String device;
        private String volumeId;
        private String id;
        private String serverId;
    }
}
