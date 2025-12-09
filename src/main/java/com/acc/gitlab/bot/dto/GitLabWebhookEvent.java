package com.acc.gitlab.bot.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class GitLabWebhookEvent {
    @JsonProperty("object_kind")
    private String objectKind;

    @JsonProperty("user")
    private User user;

    @JsonProperty("project")
    private Project project;

    @JsonProperty("merge_request")
    private MergeRequest mergeRequest;

    @JsonProperty("object_attributes")
    private ObjectAttributes objectAttributes;

    @Data
    public static class User {
        private String username;
        private String name;
        private String email;
    }

    @Data
    public static class Project {
        private Long id;
        private String name;
        @JsonProperty("web_url")
        private String webUrl;
    }

    @Data
    public static class MergeRequest {
        private Long id;
        private Long iid;
        private String title;
        private String description;
        @JsonProperty("source_branch")
        private String sourceBranch;
        @JsonProperty("target_branch")
        private String targetBranch;
    }

    @Data
    public static class ObjectAttributes {
        private Long id;
        private String note;
        @JsonProperty("noteable_type")
        private String noteableType;
        @JsonProperty("noteable_id")
        private Long noteableId;
    }
}