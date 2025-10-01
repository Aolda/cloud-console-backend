package com.acc.local.external.dto.nova;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RescueServerRequest {
    private Rescue rescue;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Rescue {
        private String adminPass;
        private String imageRef;
    }
} 