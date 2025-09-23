package com.acc.local.external.dto.neutron.conntrackhelpers;

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
public class CreateConntrackHelperRequest {
    @JsonProperty("conntrack_helper")
    private ConntrackHelper conntrackHelper;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class ConntrackHelper {
        private String protocol;
        private Integer port;
        @JsonProperty("helper")
        private String helperModule;
    }
}
