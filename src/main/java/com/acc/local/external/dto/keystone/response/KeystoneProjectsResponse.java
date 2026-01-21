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
public class KeystoneProjectsResponse {

    private List<Project> projects;
    private Links links;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Project {
        private String id;
        private String name;
        private String description;
        private Boolean enabled;

        @JsonProperty("domain_id")
        private String domainId;

        @JsonProperty("parent_id")
        private String parentId;

        @JsonProperty("is_domain")
        private Boolean isDomain;

        private Map<String, String> tags;
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