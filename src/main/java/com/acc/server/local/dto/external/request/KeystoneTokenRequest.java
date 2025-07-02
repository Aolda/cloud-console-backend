package com.acc.server.local.dto.external.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class KeystoneTokenRequest {
    @JsonProperty("auth")
    private Auth auth;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Auth {
        @JsonProperty("identity")
        private Identity identity;
        @JsonProperty("scope")
        private Scope scope;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Identity {
        @JsonProperty("methods")
        private String[] methods;
        @JsonProperty("password")
        private Password password;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Password {
        @JsonProperty("user")
        private User user;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class User {
        @JsonProperty("name")
        private String name;
        @JsonProperty("domain")
        private Domain domain;
        @JsonProperty("password")
        private String password;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Domain {
        @JsonProperty("name")
        private String name;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Scope {
        @JsonProperty("project")
        private Project project;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Project {
        @JsonProperty("name")
        private String name;
        @JsonProperty("domain")
        private Domain domain;
    }
}
