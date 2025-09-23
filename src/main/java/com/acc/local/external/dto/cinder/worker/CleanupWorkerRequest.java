package com.acc.local.external.dto.cinder.worker;

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
public class CleanupWorkerRequest {

    @JsonProperty("cluster_name")
    private String clusterName;

    @JsonProperty("service_id")
    private Integer serviceId;

    private String host;
    private String binary;

    @JsonProperty("is_up")
    private Boolean isUp;

    private Boolean disabled;

    @JsonProperty("resource_id")
    private String resourceId;

    @JsonProperty("resource_type")
    private String resourceType;
}
