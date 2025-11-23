package com.acc.local.dto.project;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;
import java.util.Map;

public record CreateProjectRequest(
        @Schema(description = "프로젝트 이름") String projectName,
        @Schema(description = "프로젝트 설명") String projectDescription,
		@Schema(description = "프로젝트 가용량") ProjectQuotaDto quota
) { }
