package com.acc.local.dto.project;

import com.acc.local.external.dto.keystone.KeystoneProject;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.Map;

@Builder
public record CreateProjectResponse(
    String projectId,
    String projectName,
    ProjectQuotaDto quota,
    String createdAt
) {
    public static CreateProjectResponse from(KeystoneProject project, ProjectQuotaDto quota) {
        return CreateProjectResponse.builder()
            .projectId(project.getId())
            .projectName(project.getName())
            .quota(quota)
            .createdAt(LocalDateTime.now().toString())
            .build();
    }
}
