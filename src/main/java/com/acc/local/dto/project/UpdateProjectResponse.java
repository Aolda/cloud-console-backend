package com.acc.local.dto.project;

import com.acc.local.external.dto.keystone.KeystoneProject;
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
    List<String> tags
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
                .build();
    }
}
