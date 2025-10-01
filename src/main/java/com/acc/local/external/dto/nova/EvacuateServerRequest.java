package com.acc.local.external.dto.nova;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EvacuateServerRequest {
    private Evacuate evacuate;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Evacuate {
        private String host;
        private String adminPass;
        private Boolean onSharedStorage;
    }
} 