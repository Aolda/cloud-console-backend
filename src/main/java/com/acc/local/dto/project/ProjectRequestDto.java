package com.acc.local.dto.project;

import java.time.LocalDateTime;

import com.acc.local.domain.enums.project.ProjectRequestStatus;
import com.acc.local.domain.enums.project.ProjectRequestType;
import com.acc.local.dto.project.quota.ProjectGlobalQuotaDto;
import com.acc.local.dto.project.quota.ProjectQuotaRequest;
import com.acc.local.entity.ProjectRequestEntity;

import lombok.Builder;
import org.jetbrains.annotations.NotNull;

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

    public ProjectCreateDto toProjectCreateDto(String approvedUserId) {
		return ProjectCreateDto.builder()
				.projectOwnerId(requestUserId)
				.projectDescription(getProjectDescriptionMessage(projectRequestId, approvedUserId))
				.projectName(projectName)
				.projectType(projectType)
				.quota(ProjectQuotaRequest.from(projectBrief))
				.build();
    }

	@NotNull
	public static String getProjectDescriptionMessage(String projectRequestId, String approvedUserId) {
		return String.format("[ACC Console] Approved by: %s | RequestId: %s", approvedUserId, projectRequestId);
	}
}
