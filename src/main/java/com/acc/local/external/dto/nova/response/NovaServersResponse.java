package com.acc.local.external.dto.nova.response;

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
public class NovaServersResponse {

    private List<Server> servers;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Server {
        private String id;
        private String name;
        private String status;
        private Flavor flavor;
        private Object image;  // can be object or string
        private Map<String, List<Address>> addresses;

        @JsonProperty("OS-EXT-STS:power_state")
        private Integer powerState;

        @JsonProperty("OS-EXT-STS:vm_state")
        private String vmState;

        @JsonProperty("OS-EXT-AZ:availability_zone")
        private String availabilityZone;

        @JsonProperty("created")
        private String created;

        @JsonProperty("updated")
        private String updated;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Flavor {
        private String id;

        @JsonProperty("original_name")
        private String originalName;

        private Integer vcpus;
        private Integer ram;
        private Integer disk;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Address {
        private String addr;

        @JsonProperty("OS-EXT-IPS:type")
        private String type;  // "fixed" or "floating"

        @JsonProperty("OS-EXT-IPS-MAC:mac_addr")
        private String macAddr;

        private Integer version;  // IP version (4 or 6)
    }
}