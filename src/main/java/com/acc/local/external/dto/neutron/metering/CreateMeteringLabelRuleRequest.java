package com.acc.local.external.dto.neutron.metering;

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
public class CreateMeteringLabelRuleRequest {
    @JsonProperty("metering_label_rule")
    private MeteringLabelRule meteringLabelRule;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class MeteringLabelRule {
        private String direction;
        @JsonProperty("metering_label_id")
        private String meteringLabelId;
        @JsonProperty("remote_ip_prefix")
        private String remoteIpPrefix;
        private Boolean excluded;
        @JsonProperty("project_id")
        private String projectId;
    }
}
