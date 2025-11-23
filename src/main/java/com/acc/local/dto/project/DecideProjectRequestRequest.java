package com.acc.local.dto.project;

import java.util.List;

import com.acc.local.domain.enums.project.ProjectRequestStatus;

import io.swagger.v3.oas.annotations.media.Schema;

public record DecideProjectRequestRequest(
	List<String> projectRequestIds, // 결정을 적용할 프로젝트 요청ID
	ProjectRequestStatus status, // 프로젝트 생성요청 결정
	String reason // 프로젝트 생성 반려사유
) {}
