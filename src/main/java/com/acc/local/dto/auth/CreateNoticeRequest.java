package com.acc.local.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
public record CreateNoticeRequest(
        @Schema(description = "공지 제목", requiredMode = Schema.RequiredMode.REQUIRED)
        String title,
        @Schema(description = "공지 내용", requiredMode = Schema.RequiredMode.REQUIRED)
        String content,
        @Schema(description = "공지 시작 시각 (ISO-8601)", example = "2025-01-01T09:00:00", requiredMode = Schema.RequiredMode.REQUIRED)
        String startsAt,
        @Schema(description = "공지 종료 시각 (ISO-8601)", example = "2025-01-31T18:00:00", requiredMode = Schema.RequiredMode.REQUIRED)
        String endsAt
) {
}
