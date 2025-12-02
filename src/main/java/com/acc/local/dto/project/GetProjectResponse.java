package com.acc.local.dto.project;

import com.acc.local.domain.enums.project.ProjectRequestStatus;
import com.acc.local.domain.enums.project.ProjectRequestType;
import com.acc.local.dto.project.quota.ProjectGlobalQuotaDto;

import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

@Builder
public record GetProjectResponse(
    String projectId,
    String projectName,
    String description,
    boolean isActive,
    ProjectRequestType projectType,
    String ownerKeystoneId,
    LocalDateTime createdAt,
    ProjectRequestStatus status,
    ProjectGlobalQuotaDto quota,
    List<ProjectParticipantDto> participants
) {
    public static GetProjectResponse from(ProjectServiceDto project) {
        return GetProjectResponse.builder()
            .projectId(project.projectId())
            .projectName(project.projectName())
            .description(project.description())
            .isActive(project.isActive())
            .projectType(project.projectType())
            .ownerKeystoneId(project.ownerKeystoneId())
            .createdAt(project.createdAt())
            .status(project.status())
            .quota(project.quota())
            .participants(project.participants())
            .build();
    }
}
