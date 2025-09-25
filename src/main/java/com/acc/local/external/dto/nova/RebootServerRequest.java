package com.acc.local.external.dto.nova;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RebootServerRequest {
    private Reboot reboot;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Reboot {
        private String type; // "SOFT" or "HARD"
    }
} 