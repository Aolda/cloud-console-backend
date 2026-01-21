package com.acc.local.external.dto.nova.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class NovaLimitsResponse {
    private Limits limits;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Limits {
        private Absolute absolute;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Absolute {
        private Integer maxCores;
        private Integer maxInstances;
        private Integer maxTotalRAMSize;
        private Integer totalCoresUsed;
        private Integer totalInstancesUsed;
        private Integer totalRAMUsed;
    }
}

