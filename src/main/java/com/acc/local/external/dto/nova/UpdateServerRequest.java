package com.acc.local.external.dto.nova;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateServerRequest {
    private Server server;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Server {
        private String name;
        private String accessIPv4;
        private String accessIPv6;
        private Map<String, String> metadata;
    }
} 