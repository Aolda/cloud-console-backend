package com.acc.local.dto.project;

import com.acc.local.domain.enums.project.ProjectRequestStatus;
import com.acc.local.domain.enums.project.ProjectRequestType;
import com.acc.local.dto.auth.UserKeystoneDto;
import com.acc.local.dto.project.quota.ProjectGlobalQuotaDto;

import lombok.Builder;

@Builder
public record ProjectRequestResponse(
	String projectRequestId,
	String projectName,
	ProjectRequestType projectType,
	ProjectOwnerDto createdBy,
	String createdAt,
	ProjectRequestStatus status,
	ProjectGlobalQuotaDto quota
) {
	public static ProjectRequestResponse from(ProjectRequestDto projectRequest, UserKeystoneDto createdBy) {
		ProjectGlobalQuotaDto quota = projectRequest.quota() == null
			? ProjectGlobalQuotaDto.getDefault()
			: projectRequest.quota();
		return ProjectRequestResponse.builder()
			.projectRequestId(projectRequest.projectRequestId())
			.projectName(projectRequest.projectName())
			.projectType(projectRequest.projectType())
			.createdBy(ProjectOwnerDto.from(createdBy))
			.createdAt(projectRequest.createdAt().toString())
			.status(projectRequest.status())
			.quota(quota)
			.build();
	}
}
