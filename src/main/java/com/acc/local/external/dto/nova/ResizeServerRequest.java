package com.acc.local.external.dto.nova;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResizeServerRequest {
    private Resize resize;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Resize {
        private String flavorRef;
        private String diskConfig; // 변수명 변환 필요
    }
} 