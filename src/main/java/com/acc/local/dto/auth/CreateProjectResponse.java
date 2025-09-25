package com.acc.local.dto.auth;

import com.acc.local.domain.model.KeystoneProject;
import lombok.Builder;

import java.util.Map;

@Builder
public record CreateProjectResponse(
    String id,
    String name,
    String description,
    String domainId,
    Boolean enabled,
    Boolean isDomain,
    String parentId,
    Map<String, Object> options,
    Map<String, Object> links
) {
    public static CreateProjectResponse from(KeystoneProject project) {
        return CreateProjectResponse.builder()
                .id(project.getId())
                .name(project.getName())
                .description(project.getDescription())
                .domainId(project.getDomainId())
                .enabled(project.getEnabled())
                .isDomain(project.getIsDomain())
                .options(project.getOptions())
                .links(project.getLinks())
                .build();
    }
}