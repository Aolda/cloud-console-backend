package com.acc.local.dto.project;

import java.time.LocalDateTime;
import java.util.List;

import com.acc.local.domain.enums.project.ProjectRequestStatus;
import com.acc.local.domain.enums.project.ProjectRequestType;
import com.acc.local.domain.enums.project.ProjectRole;
import com.acc.local.domain.model.auth.KeystoneUser;
import com.acc.local.dto.project.quota.ProjectGlobalQuotaDto;

import lombok.Builder;

@Builder
public record ProjectResponse(
	String projectId,
	String projectName,
	ProjectRequestType projectType,
	ProjectOwnerDto createdBy,
	String createdAt,
	ProjectRequestStatus status,
	@Deprecated ProjectGlobalQuotaDto projectBrief,
	ProjectGlobalQuotaDto quota,
	List<ProjectParticipantDto> participants,
	String rejectReason
) {

	// 생성된 프로젝트
	public static ProjectResponse from(ProjectServiceDto projectServiceDto, KeystoneUser owner, List<ProjectParticipantDto> participants) {
		ProjectRequestType projectType = projectServiceDto.projectType();
		if (projectType == null) {
			projectType = ProjectRequestType.ETC;
		}

		if (participants.isEmpty() && owner != null) {
			participants.add(
				ProjectParticipantDto.builder()
					.userId(owner.getId())
					.userName(owner.getName())
					.userEmail(owner.getEmail())
					.role(ProjectRole.PROJECT_ADMIN)
				.build()
			);
		}

		ProjectGlobalQuotaDto quota = projectServiceDto.quota();
		if (quota == null) {
			quota = ProjectGlobalQuotaDto.getDefault();
		}

		String createdAt = LocalDateTime.of(1900, 1, 1, 0, 0).toString();
		if (projectServiceDto.createdAt() != null) {
			createdAt = projectServiceDto.createdAt().toString();
		}

		return ProjectResponse.builder()
			.projectId(projectServiceDto.projectId())
			.projectName(projectServiceDto.projectName())
			.projectType(projectType)
			.createdBy(owner == null ? null : ProjectOwnerDto.from(owner))
			.createdAt(createdAt)
			.status(projectServiceDto.status())
			.projectBrief(quota)
			.quota(quota)
			.participants(participants)
			.build();
	}

	// 프로젝트 요청
	public static ProjectResponse from(ProjectRequestDto projectRequestDto, KeystoneUser projectRequestUser) {
		return ProjectResponse.builder()
			.projectName(projectRequestDto.projectName())
			.projectType(projectRequestDto.projectType())
			.createdBy(ProjectOwnerDto.from(projectRequestUser))
			.createdAt(projectRequestDto.createdAt().toString())
			.status(projectRequestDto.status())
			.projectBrief(ProjectGlobalQuotaDto.getDefault())
			.quota(ProjectGlobalQuotaDto.getDefault())
			.rejectReason(projectRequestDto.rejectReason())
			.participants(List.of(
				ProjectParticipantDto.builder()
					.userId(projectRequestUser.getId())
					.userName(projectRequestUser.getName())
					.role(ProjectRole.PROJECT_ADMIN)
					.build()
			))
			.build();
	}
}
