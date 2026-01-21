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
public class KeystoneUsersResponse {

    private List<User> users;
    private Links links;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class User {
        private String id;
        private String name;
        private String email;
        private Boolean enabled;

        @JsonProperty("domain_id")
        private String domainId;

        @JsonProperty("default_project_id")
        private String defaultProjectId;

        private String description;

        @JsonProperty("password_expires_at")
        private String passwordExpiresAt;

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