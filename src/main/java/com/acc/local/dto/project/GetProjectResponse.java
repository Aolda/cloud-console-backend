package com.acc.local.dto.project;

import com.acc.local.external.dto.keystone.KeystoneProject;
import lombok.Builder;

import java.util.Map;

@Builder
public record GetProjectResponse(
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
    public static GetProjectResponse from(KeystoneProject project) {
        return GetProjectResponse.builder()
                .id(project.getId())
                .name(project.getName())
                .description(project.getDescription())
                .domainId(project.getDomainId())
                .enabled(project.getEnabled())
                .isDomain(project.getIsDomain())
                .parentId(project.getParentId())
                .build();
    }
}
