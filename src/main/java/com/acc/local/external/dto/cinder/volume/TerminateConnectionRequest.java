package com.acc.local.external.dto.cinder.volume;

import com.acc.local.external.dto.cinder.common.ConnectorDto;
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
public class TerminateConnectionRequest {

    @JsonProperty("os-terminate_connection")
    private OsTerminateConnection osTerminateConnection;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class OsTerminateConnection {
        @JsonProperty("connector")
        private ConnectorDto connector;
    }
}
