package com.acc.local.external.dto.nova;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AddHostToAggregateRequest {
    private AddHost add_host;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class AddHost {
        private String host;
    }
}
