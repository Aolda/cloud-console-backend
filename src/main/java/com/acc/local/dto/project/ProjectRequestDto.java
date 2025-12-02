package com.acc.local.dto.project;

import java.time.LocalDateTime;

import com.acc.local.domain.enums.project.ProjectRequestStatus;
import com.acc.local.domain.enums.project.ProjectRequestType;
import com.acc.local.entity.ProjectRequestEntity;

import lombok.Builder;

@Builder
public record ProjectRequestDto(
	String projectRequestId,
	String projectName,
	String requestUserId,
	String description,
	ProjectRequestType projectType,
	LocalDateTime createdAt,
	ProjectRequestStatus status,
	String rejectReason,
	ProjectGlobalQuotaDto projectBrief
) {
	public static ProjectRequestDto from(ProjectRequestEntity projectRequestEntity) {
		return ProjectRequestDto.builder()
			.projectRequestId(projectRequestEntity.getProjectRequestId())
			.projectName(projectRequestEntity.getProjectName())
			.requestUserId(projectRequestEntity.getRequestUserId())
			.description(projectRequestEntity.getProjectDescription())
			.projectType(projectRequestEntity.getProjectType())
			.createdAt(projectRequestEntity.getCreatedAt())
			.status(projectRequestEntity.getStatus())
			.rejectReason(projectRequestEntity.getRejectReason())
			.projectBrief(ProjectGlobalQuotaDto.getDefault())
			.build();
	}
}
