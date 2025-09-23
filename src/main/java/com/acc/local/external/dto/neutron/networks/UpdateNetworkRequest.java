package com.acc.local.external.dto.neutron.networks;

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
public class UpdateNetworkRequest {
    private Network network;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Network {
        private String name;
        private String description;
        @JsonProperty("admin_state_up")
        private Boolean adminStateUp;
        private Boolean shared;
        @JsonProperty("port_security_enabled")
        private Boolean portSecurityEnabled;
        @JsonProperty("qos_policy_id")
        private String qosPolicyId;
        private List<String> tags;
    }
}
