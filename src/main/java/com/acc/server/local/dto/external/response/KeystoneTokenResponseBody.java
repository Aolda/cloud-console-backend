package com.acc.server.local.dto.external.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.util.List;

@Data
public class KeystoneTokenResponseBody {

    @JsonProperty("token")
    private Token token;

    @Data
    public static class Token {
        @JsonProperty("expires_at")
        private String expiresAt;

        @JsonProperty("issued_at")
        private String issuedAt;

        @JsonProperty("user")
        private User user;

        @JsonProperty("project")
        private Project project;

        @JsonProperty("roles")
        private List<Role> roles;
    }

    @Data
    public static class User {
        @JsonProperty("id")
        private String id;

        @JsonProperty("name")
        private String name;

        @JsonProperty("domain")
        private Domain domain;
    }

    @Data
    public static class Project {
        @JsonProperty("id")
        private String id;

        @JsonProperty("name")
        private String name;

        @JsonProperty("domain")
        private Domain domain;
    }

    @Data
    public static class Domain {
        @JsonProperty("id")
        private String id;

        @JsonProperty("name")
        private String name;
    }

    @Data
    public static class Role {
        @JsonProperty("id")
        private String id;

        @JsonProperty("name")
        private String name;
    }
}
