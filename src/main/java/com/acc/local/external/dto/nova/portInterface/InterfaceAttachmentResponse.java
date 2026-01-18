package com.acc.local.external.dto.nova.portInterface;

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
public class InterfaceAttachmentResponse {
    @JsonProperty("interfaceAttachment")
    private InterfaceAttachment interfaceAttachment;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class InterfaceAttachment {
        @JsonProperty("port_id")
        private String portId;

        @JsonProperty("net_id")
        private String netId;

        @JsonProperty("mac_addr")
        private String macAddr;

        @JsonProperty("port_state")
        private String portState;

        @JsonProperty("fixed_ips")
        private List<FixedIp> fixedIps;

        @JsonProperty("tag")
        private String tag;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class FixedIp {
        @JsonProperty("ip_address")
        private String ipAddress;

        @JsonProperty("subnet_id")
        private String subnetId;
    }
}

