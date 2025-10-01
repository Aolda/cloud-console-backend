package com.acc.local.external.dto.nova;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SetAggregateMetadata {
    private Metadata set_metadata;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Metadata {
        private String key;
    }
}
