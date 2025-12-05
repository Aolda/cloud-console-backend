package com.acc.local.dto.project;

import java.util.List;

import com.acc.local.domain.enums.project.ProjectRequestStatus;
import com.acc.local.domain.enums.project.ProjectRequestType;
import com.acc.local.entity.ProjectRequestEntity;

import lombok.Builder;

@Builder
public record CreateProjectRequestResponse(
	String projectRequestId,
	String projectName,
	ProjectRequestType projectType,
	String createdAt,
	ProjectRequestStatus status,
	List<ProjectParticipantDto> participants
) {
	public static CreateProjectRequestResponse from(ProjectRequestEntity projectRequestEntity) {
		return CreateProjectRequestResponse.builder()
			.projectRequestId(projectRequestEntity.getProjectRequestId())
			.projectName(projectRequestEntity.getProjectName())
			.projectType(projectRequestEntity.getProjectType())
			.createdAt(projectRequestEntity.getCreatedAt().toString())
			.status(projectRequestEntity.getStatus())
			.build();
	}
}
