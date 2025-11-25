package com.acc.local.dto.project;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;

public record KickOutProjectRequest(
	@Schema(description = "프로젝트 참여권한을 삭제할 사용자 목록") List<String> userIds
) {}
