package com.acc.local.dto.project;

import com.acc.local.domain.enums.project.ProjectRequestType;

import io.swagger.v3.oas.annotations.media.Schema;

public record CreateProjectRequestRequest(
	@Schema(description = "프로젝트 이름 (영어 소문자, 숫자, '-'만 허용)") String projectName,
	@Schema(description = "프로젝트 유형") ProjectRequestType projectType,
	@Schema(description = "프로젝트 설명 (최대 10,000자)") String projectDescription
) {}
