package com.acc.local.dto.project;

import java.time.LocalDateTime;
import java.util.List;

import com.acc.local.domain.enums.project.ProjectRequestStatus;
import com.acc.local.domain.enums.project.ProjectRequestType;
import com.acc.local.dto.project.quota.ProjectGlobalQuotaDto;
import com.acc.local.entity.ProjectEntity;
import com.acc.local.external.dto.keystone.KeystoneProject;

import lombok.Builder;

@Builder
public record ProjectServiceDto(
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
	public static ProjectServiceDto from(ProjectEntity dbProject, KeystoneProject keystoneProject) { // TODO: AdminGetUserResponse로 변경 필요 (데이터 부족)

		ProjectGlobalQuotaDto projectGlobalQuotaDto = null;
		if (dbProject.getQuotaVCpuCount() != null) {
			ProjectGlobalQuotaDto.builder()
				.vCpu(Integer.parseInt(String.valueOf(dbProject.getQuotaVCpuCount())))
				.vRam(Integer.parseInt(String.valueOf(dbProject.getQuotaVRamMB())))
				.storage(Integer.parseInt(String.valueOf(dbProject.getQuotaStorageGB())))
				.instance(Integer.parseInt(String.valueOf(dbProject.getQuotaInstanceCount())))
				.build();
		}

		return ProjectServiceDto.builder()
			.projectId(keystoneProject.getId())
			.projectName(keystoneProject.getName())
			.description(keystoneProject.getDescription())
			.isActive(keystoneProject.getEnabled())
			.projectType(dbProject.getProjectType())
			.ownerKeystoneId(dbProject.getOwnerKeystoneId())
			.createdAt(dbProject.getCreatedAt())
			.status(ProjectRequestStatus.APPROVED)
			.quota(projectGlobalQuotaDto)
			.participants(
				dbProject.getParticipants().stream()
					.map(ProjectParticipantDto::from)
					.toList()
			)
			.build();
	}
}
