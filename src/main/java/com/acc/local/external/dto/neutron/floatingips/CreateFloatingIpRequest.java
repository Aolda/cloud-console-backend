package com.acc.local.external.dto.neutron.floatingips;

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
public class CreateFloatingIpRequest {
    private FloatingIp floatingip;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class FloatingIp {
        @JsonProperty("floating_network_id")
        private String floatingNetworkId;
        @JsonProperty("port_id")
        private String portId;
        @JsonProperty("fixed_ip_address")
        private String fixedIpAddress;
        @JsonProperty("floating_ip_address")
        private String floatingIpAddress;
        @JsonProperty("subnet_id")
        private String subnetId;
        private String description;
        @JsonProperty("project_id")
        private String projectId;
        private List<String> tags;
    }
}
