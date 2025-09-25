package com.acc.local.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;
import java.util.Map;

public record CreateProjectRequest(
        @Schema(description = "프로젝트 이름") String name,
        @Schema(description = "도메인 역할 여부") Boolean isDomain,
        @Schema(description = "프로젝트 설명") String description,
        @Schema(description = "도메인 ID") String domainId,
        @Schema(description = "활성화 여부") Boolean enabled,
        @Schema(description = "상위 프로젝트 ID") String parentId,
        @Schema(description = "태그 목록") List<String> tags,
        @Schema(description = "프로젝트 옵션") Map<String, Object> options
) { }