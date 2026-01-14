package com.acc.local.dto.project;

import java.util.List;

import com.acc.local.domain.enums.project.ProjectRequestStatus;

import io.swagger.v3.oas.annotations.media.Schema;

public record DecideProjectRequestRequest(
	@Schema(description = "결정을 적용할 프로젝트 요청 ID 목록", requiredMode = Schema.RequiredMode.REQUIRED)
	List<String> projectRequestIds,

	@Schema(description = "요청 결정 상태 (APPROVED|REJECTED)", requiredMode = Schema.RequiredMode.REQUIRED, example = "APPROVED")
	ProjectRequestStatus status,

	@Schema(description = "거절 사유(선택). status=REJECTED 인 경우 필수")
	String reason
) {}
