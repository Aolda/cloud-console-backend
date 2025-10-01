package com.acc.local.external.dto.neutron.portforwardings;

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
public class CreatePortForwardingRequest {

    @JsonProperty("port_forwarding")
    private PortForwarding portForwarding;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class PortForwarding {
        @JsonProperty("internal_port_id")
        private String internalPortId;
        @JsonProperty("internal_ip_address")
        private String internalIpAddress;
        @JsonProperty("internal_port")
        private Integer internalPort;
        @JsonProperty("external_port")
        private Integer externalPort;
        private String protocol;
    }
}
