package com.acc.local.dto.project;

import com.acc.local.domain.enums.project.ProjectRequestStatus;
import com.acc.local.domain.enums.project.ProjectRequestType;
import com.acc.local.domain.model.auth.User;
import com.acc.local.entity.ProjectRequestEntity;

import lombok.Builder;

@Builder
public record ProjectRequestResponse(
	String projectRequestId,
	String projectName,
	ProjectRequestType projectType,
	ProjectOwnerDto createdBy,
	String createdAt,
	ProjectRequestStatus status,
	ProjectQuotaDto projectBrief
) {
	public static ProjectRequestResponse from(ProjectRequestDto projectRequest, User createdBy) {
		return ProjectRequestResponse.builder()
			.projectRequestId(projectRequest.projectRequestId())
			.projectName(projectRequest.projectName())
			.projectType(projectRequest.projectType())
			.createdBy(ProjectOwnerDto.from(createdBy))
			.createdAt(projectRequest.createdAt().toString())
			.status(projectRequest.status())
			.projectBrief(ProjectQuotaDto.getDefault())
			.build();
	}
}
