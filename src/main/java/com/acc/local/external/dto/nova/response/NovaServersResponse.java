package com.acc.local.external.dto.nova.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class NovaServersResponse {
    private List<Server> servers;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Server {
        private String id;
        private String name;
        private String status;

        // addresses: { "net-name": [ { "addr": "...", "OS-EXT-IPS:type": "fixed|floating" }, ... ] }
        private Map<String, List<Address>> addresses;

        // image can be String (id) or object; keep as Object for flexibility
        private Object image;

        private Flavor flavor;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Address {
        private String addr;

        @JsonProperty("OS-EXT-IPS:type")
        private String type;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Flavor {
        private String id;
        private String name;

        @JsonProperty("original_name")
        private String originalName;
    }
}
