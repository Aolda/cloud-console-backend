package com.acc.local.dto.project;

import java.util.List;

import com.acc.local.domain.enums.project.ProjectRequestStatus;
import com.acc.local.domain.enums.project.ProjectRequestType;
import com.acc.local.domain.model.auth.KeystoneUser;

import lombok.Builder;

@Builder
public record ProjectResponse(
	String projectId,
	String projectName,
	ProjectRequestType projectType,
	ProjectOwnerDto createdBy,
	String createdAt,
	ProjectRequestStatus status,
	ProjectQuotaDto projectBrief,
	List<ProjectParticipantDto> participants
) {
	public static ProjectResponse from(ProjectServiceDto projectServiceDto, KeystoneUser owner, List<ProjectParticipantDto> participants) {
		return ProjectResponse.builder()
			.projectId(projectServiceDto.projectId())
			.projectName(projectServiceDto.projectName())
			.projectType(projectServiceDto.projectType())
			.createdBy(ProjectOwnerDto.from(owner))
			.createdAt(projectServiceDto.createdAt().toString())
			.status(projectServiceDto.status())
			.projectBrief(projectServiceDto.quota())
			.participants(participants)
			.build();
	}
}
