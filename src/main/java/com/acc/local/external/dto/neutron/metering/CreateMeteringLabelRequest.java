package com.acc.local.external.dto.neutron.metering;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CreateMeteringLabelRequest {
    @JsonProperty("metering_label")
    private MeteringLabel meteringLabel;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class MeteringLabel {
        private String name;
        private String description;
        private Boolean shared;
        @JsonProperty("project_id")
        private String projectId;
        private List<String> tags;
    }
}
