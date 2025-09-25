package com.acc.local.external.dto.nova;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AttachVolumeRequest {
    private List<VolumeAttachment> volumeAttachment;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class VolumeAttachment {
        private String volumeId;
        private String device;
    }
} 