package com.acc.local.external.dto.glance.image;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.ArrayList;

@Data
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GlanceUpdateImageRequest extends ArrayList<GlanceUpdateImageRequest.Operation> {
// body 가 배열

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Operation {
        @JsonProperty("op")
        private String op;

        @JsonProperty("path")
        private String path;

        @JsonProperty("value")
        private Object value;

        @JsonProperty("from")
        private String from;
    }
}
