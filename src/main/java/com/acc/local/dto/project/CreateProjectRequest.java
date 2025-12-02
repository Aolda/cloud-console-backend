package com.acc.local.dto.project;

import com.acc.local.dto.project.quota.ProjectQuotaRequest;

import io.swagger.v3.oas.annotations.media.Schema;

public record CreateProjectRequest(
        @Schema(description = "프로젝트 이름") String projectName,
        @Schema(description = "프로젝트 설명") String projectDescription,
		@Schema(description = "프로젝트 가용량") ProjectQuotaRequest quota,
        @Schema(description = "프로젝트 담당자 ID") String projectOwnerId
) { }
