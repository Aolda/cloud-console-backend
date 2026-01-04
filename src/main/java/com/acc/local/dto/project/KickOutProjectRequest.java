package com.acc.local.dto.project;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;

public record KickOutProjectRequest(
	@Schema(description = "프로젝트에서 제거할 사용자 ID 목록", requiredMode = Schema.RequiredMode.REQUIRED)
	List<String> userIds
) {}
