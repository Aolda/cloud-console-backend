package com.acc.local.dto.project;

import com.acc.local.external.dto.keystone.KeystoneProject;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record CreateProjectResponse(
    String projectId,
    String projectName,
    ProjectGlobalQuotaDto quota,
    String createdAt
) {
    public static CreateProjectResponse from(KeystoneProject project, ProjectGlobalQuotaDto quota) {
        return CreateProjectResponse.builder()
            .projectId(project.getId())
            .projectName(project.getName())
            .quota(quota)
            .createdAt(LocalDateTime.now().toString())
            .build();
    }
}
