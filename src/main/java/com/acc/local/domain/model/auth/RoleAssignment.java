package com.acc.local.domain.model.auth;

import lombok.Builder;
import lombok.Getter;

import java.util.Map;

@Getter
@Builder
public class RoleAssignment {

    private RoleInfo role;
    private UserInfo user;
    private GroupInfo group;
    private ScopeInfo scope;
    private Map<String, String> links;

    @Getter
    @Builder
    public static class RoleInfo {
        private String id;
        private String name;
    }

    @Getter
    @Builder
    public static class UserInfo {
        private String id;
        private String name;
        private DomainInfo domain;
    }

    @Getter
    @Builder
    public static class GroupInfo {
        private String id;
        private String name;
        private DomainInfo domain;
    }

    @Getter
    @Builder
    public static class DomainInfo {
        private String id;
        private String name;
    }

    @Getter
    @Builder
    public static class ScopeInfo {
        private ProjectInfo project;
        private DomainInfo domain;
        private SystemInfo system;
    }

    @Getter
    @Builder
    public static class ProjectInfo {
        private String id;
        private String name;
        private DomainInfo domain;
    }

    @Getter
    @Builder
    public static class SystemInfo {
        private Boolean all;
    }
}