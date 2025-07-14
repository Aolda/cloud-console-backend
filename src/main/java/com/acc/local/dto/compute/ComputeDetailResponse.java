package com.acc.local.dto.compute;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

public record ComputeDetailResponse(
        String id,
        String name,
        String status,
        @JsonProperty("tenant_id") String tenantId,
        @JsonProperty("user_id") String userId,
        Map<String, List<ComputeAddress>> addresses,
        ComputeFlavor flavor,
        ComputeImage image,
        List<ComputeLink> links,
        @JsonProperty("OS-EXT-STS:power_state") int powerState,
        @JsonProperty("OS-EXT-SRV-ATTR:host") String host,
        @JsonProperty("OS-EXT-SRV-ATTR:instance_name") String instanceName,
        @JsonProperty("OS-EXT-SRV-ATTR:hypervisor_hostname") String hypervisorHostname
) {}
