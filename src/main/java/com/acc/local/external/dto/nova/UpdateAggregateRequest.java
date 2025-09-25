package com.acc.local.external.dto.nova;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateAggregateRequest {
    private Aggregate aggregate;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Aggregate {
        private String name;
        private String availability_zone;
    }
}
