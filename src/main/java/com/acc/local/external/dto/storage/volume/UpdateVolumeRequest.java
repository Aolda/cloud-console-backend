package com.acc.local.external.dto.storage.volume;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateVolumeRequest {

    private Volume volume;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Volume {
        private String name;
        private String description;
        private Map<String, String> metadata;
    }
}
