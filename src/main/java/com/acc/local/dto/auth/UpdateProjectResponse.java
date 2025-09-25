package com.acc.local.dto.auth;

import com.acc.local.domain.model.KeystoneProject;
import lombok.Builder;

import java.util.List;
import java.util.Map;

@Builder
public record UpdateProjectResponse(
    String id,
    String name,
    String description,
    String domainId,
    Boolean enabled,
    Boolean isDomain,
    String parentId,
    List<String> tags,
    Map<String, Object> options,
    Map<String, Object> links
) {
    public static UpdateProjectResponse from(KeystoneProject project) {
        return UpdateProjectResponse.builder()
                .id(project.getId())
                .name(project.getName())
                .description(project.getDescription())
                .domainId(project.getDomainId())
                .enabled(project.getEnabled())
                .isDomain(project.getIsDomain())
                .parentId(project.getParentId())
                .tags(project.getTags())
                .options(project.getOptions())
                .links(project.getLinks())
                .build();
    }
}