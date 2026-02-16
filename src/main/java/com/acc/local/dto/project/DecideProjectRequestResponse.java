package com.acc.local.dto.project;

import com.acc.local.dto.project.decision.DecisionApplyInfo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.util.Map;

@Builder
public record DecideProjectRequestResponse(
	@Schema(description = "결정적용을 요청한 프로젝트요청 수")
	int requested,

	@Schema(description = "실제로 존재하는 프로젝트요청임이 확인된 프로젝트요청 수")
	int acknowledged,

	@Schema(description = "실제로 요청한 결정이 적용된 프로젝트요청 수")
	int applied,

	@Schema(description = "각 요청건 별 상세처리정보")
	Map<String, DecisionApplyInfo> data
) {}
