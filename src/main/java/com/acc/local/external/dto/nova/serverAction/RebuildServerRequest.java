package com.acc.local.external.dto.nova.serverAction;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RebuildServerRequest {

    private RebuildInfo rebuild;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class RebuildInfo {

        @JsonProperty("imageRef")
        private String imageRef;

        private String name;
        private String adminPass;
        private Map<String, String> metadata;

        @JsonProperty("OS-DCF:diskConfig")
        private String diskConfig;
    }
}
