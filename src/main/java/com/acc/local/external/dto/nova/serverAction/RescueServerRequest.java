package com.acc.local.external.dto.nova.serverAction;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RescueServerRequest {
    private RescueInfo rescue;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class RescueInfo {
        private String adminPass;

        @JsonProperty("rescue_image_ref")
        private String rescueImageRef;
    }
}
