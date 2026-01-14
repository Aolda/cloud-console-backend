package com.acc.local.dto.project;

import java.util.List;

import com.acc.local.domain.enums.project.ProjectRole;

import io.swagger.v3.oas.annotations.media.Schema;

public record InviteProjectRequest(
	@Schema(description = "초대 사용자 ID 목록", requiredMode = Schema.RequiredMode.REQUIRED)
	List<String> userIds,

	@Schema(description = "부여할 프로젝트 권한", requiredMode = Schema.RequiredMode.REQUIRED, example = "PROJECT_MEMBER")
	ProjectRole role
	) {}
