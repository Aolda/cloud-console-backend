package com.acc.local.external.dto.neutron.logging;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CreateLogRequest {
    private Log log;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Log {
        @JsonProperty("resource_type")
        private String resourceType;
        @JsonProperty("resource_id")
        private String resourceId;
        private String event;
        private Boolean enabled;
        @JsonProperty("project_id")
        private String projectId;
    }
}
