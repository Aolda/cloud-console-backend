package com.acc.local.external.dto.neutron.sfc;

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
public class CreateFlowClassifierRequest {
    @JsonProperty("flow_classifier")
    private FlowClassifier flowClassifier;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class FlowClassifier {
        private String name;
        private String description;
        private String protocol;
        @JsonProperty("source_port_range_min")
        private Integer sourcePortRangeMin;
        @JsonProperty("source_port_range_max")
        private Integer sourcePortRangeMax;
        @JsonProperty("destination_port_range_min")
        private Integer destinationPortRangeMin;
        @JsonProperty("destination_port_range_max")
        private Integer destinationPortRangeMax;
        @JsonProperty("source_ip_prefix")
        private String sourceIpPrefix;
        @JsonProperty("destination_ip_prefix")
        private String destinationIpPrefix;
        @JsonProperty("logical_source_port")
        private String logicalSourcePort;
        @JsonProperty("logical_destination_port")
        private String logicalDestinationPort;
        private String ethertype;
        @JsonProperty("project_id")
        private String projectId;
    }
}
