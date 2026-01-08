package com.acc.local.external.dto.nova.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NovaFlavorsResponse {

    private List<Flavor> flavors;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Flavor {
        private String id;
        private String name;

        @JsonProperty("original_name")
        private String originalName;

        private Integer vcpus;
        private Integer ram;
        private Integer disk;

        @JsonProperty("OS-FLV-DISABLED:disabled")
        private Boolean disabled;

        @JsonProperty("OS-FLV-EXT-DATA:ephemeral")
        private Integer ephemeral;

        @JsonProperty("os-flavor-access:is_public")
        private Boolean isPublic;

        private Integer swap;
    }
}