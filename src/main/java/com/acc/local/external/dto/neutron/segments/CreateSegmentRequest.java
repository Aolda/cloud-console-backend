package com.acc.local.external.dto.neutron.segments;

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
public class CreateSegmentRequest {

    private Segment segment;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Segment {
        @JsonProperty("network_id")
        private String networkId;
        private String name;
        private String description;
        @JsonProperty("network_type")
        private String networkType;
        @JsonProperty("physical_network")
        private String physicalNetwork;
        @JsonProperty("segmentation_id")
        private Integer segmentationId;
    }
}
