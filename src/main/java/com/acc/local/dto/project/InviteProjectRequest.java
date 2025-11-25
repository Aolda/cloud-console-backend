package com.acc.local.dto.project;

import java.util.List;

import com.acc.local.domain.enums.project.ProjectRole;

import io.swagger.v3.oas.annotations.media.Schema;

public record InviteProjectRequest(
	@Schema(description = "초대 사용자 ID") List<String> userIds,
	@Schema(description = "프로젝트 초대권한") ProjectRole role
	) {}
