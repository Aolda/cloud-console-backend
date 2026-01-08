package com.acc.local.external.dto.keystone.response;

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
public class KeystoneRolesResponse {

    private List<Role> roles;
    private Links links;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Role {
        private String id;
        private String name;
        private String description;

        @JsonProperty("domain_id")
        private String domainId;

        private Map<String, Object> options;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Links {
        private String self;
        private String previous;
        private String next;
    }
}