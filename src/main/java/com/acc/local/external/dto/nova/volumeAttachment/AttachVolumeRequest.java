package com.acc.local.external.dto.nova.volumeAttachment;

import java.util.List;

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
public class AttachVolumeRequest {
    private List<VolumeAttachment> volumeAttachment;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class VolumeAttachment {
        private String volumeId;
        private String device;
        private Boolean delete_on_termination;
    }
} 