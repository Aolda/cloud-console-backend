package com.acc.local.dto.image;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
@Schema(description = "이미지 요약 정보")
public record GlanceImageSummary(
        @Schema(description = "이미지 ID")
        String id,
        @Schema(description = "이미지 이름")
        String name,
        @Schema(description = "아키텍처")
        String architecture,
        @Schema(description = "프로젝트 이름")
        String projectName,
        @Schema(description = "이미지 설명")
        String description,
        @Schema(description = "디스크 포맷")
        String diskFormat,
        @Schema(description = "이미지 상태")
        String status,
        @Schema(description = "가시성 (public/private)")
        String visibility,
        @Schema(description = "이미지 크기(바이트)")
        Long size,
        @Schema(description = "숨김 여부")
        Boolean hidden,
        @Schema(description = "최소 디스크 크기(GiB)")
        Integer minDisk,
        @Schema(description = "최소 메모리 크기(MiB)")
        Integer minRam,
        @Schema(description = "생성 시간")
        String createdAt
) {}
