package com.acc.local.external.dto.storage.volume;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InitializeConnectionRequest {

    @JsonProperty("os-initialize_connection")
    private OsInitializeConnection osInitializeConnection;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class OsInitializeConnection {
        @JsonProperty("connector")
        private Connector connector;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Connector {
        private String platform;
        private String host;

        @JsonProperty("do_local_attach")
        private Boolean doLocalAttach;

        private String ip;

        @JsonProperty("os_type")
        private String osType;

        private Boolean multipath;
        private String initiator;
    }
}
