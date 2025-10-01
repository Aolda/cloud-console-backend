package com.acc.local.external.dto.neutron.subnetpools;

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
public class CreateSubnetPoolRequest {

    private SubnetPool subnetpool;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class SubnetPool {
        private String name;
        private String description;
        @JsonProperty("project_id")
        private String projectId;
        private List<String> prefixes;
        @JsonProperty("default_prefixlen")
        private Integer defaultPrefixlen;
        @JsonProperty("min_prefixlen")
        private Integer minPrefixlen;
        @JsonProperty("max_prefixlen")
        private Integer maxPrefixlen;
        private Boolean shared;
        private List<String> tags;
    }
}
