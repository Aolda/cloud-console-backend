package com.acc.server.local.service.modules.keystoneToken.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class KeystoneTokenRequest {

    @JsonProperty("auth")
    private Auth auth;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
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
        @JsonProperty("id")
        private String id;

        @JsonProperty("password")
        private String password;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Domain {
        @JsonProperty("name")
        private String name;

        @JsonProperty("id")
        private Long id;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Scope {
        @JsonProperty("project")
        private Project project;

        @JsonProperty("domain")
        private Domain domain;

        @JsonProperty("system")
        private SystemScope system;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Project {
        @JsonProperty("domain")
        private Domain domain;

        @JsonProperty("name")
        private String name;

        @JsonProperty("id")
        private Long id;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SystemScope {
        @JsonProperty("all")
        private Boolean all;
    }
}
