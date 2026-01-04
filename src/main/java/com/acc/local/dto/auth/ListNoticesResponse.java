package com.acc.local.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

public record ListNoticesResponse(
        String noticeId,
        String title,
        String content,
        String createdBy,
        @Schema(description = "생성 시각 (ISO-8601)", example = "2025-01-01T09:00:00")
        LocalDateTime createdAt,
        @Schema(description = "공지 시작 시각 (ISO-8601)", example = "2025-01-01T09:00:00")
        LocalDateTime startsAt,
        @Schema(description = "공지 종료 시각 (ISO-8601)", example = "2025-01-31T18:00:00")
        LocalDateTime endsAt
) {
    public static ListNoticesResponse from(String noticeId, String title, String content,
                                          String createdBy, LocalDateTime createdAt,
                                          LocalDateTime startsAt, LocalDateTime endsAt) {
        return new ListNoticesResponse(noticeId, title, content, createdBy, createdAt, startsAt, endsAt);
    }
}
