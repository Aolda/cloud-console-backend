package com.acc.local.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

public record ListNoticesResponse(
        @Schema(description = "공지 ID", example = "550e8400-e29b-41d4-a716-446655440000")
        String noticeId,

        @Schema(description = "제목", example = "장학 공지")
        String title,

        @Schema(description = "내용", example = "아올다 회원은 전액 장학을 지원합니다.")
        String content,

        @Schema(description = "작성자", example = "admin")
        String createdBy,

        @Schema(description = "생성 일시 (ISO-8601, 초 단위, KST, 타임존 미포함)", example = "2025-01-01T09:00:00")
        LocalDateTime createdAt,

        @Schema(description = "시작 일시 (ISO-8601, 초 단위, KST, 타임존 미포함)", example = "2025-01-01T00:00:00")
        LocalDateTime startsAt,

        @Schema(description = "종료 일시 (ISO-8601, 초 단위, KST, 타임존 미포함)", example = "2025-12-31T23:59:59")
        LocalDateTime endsAt
) {
    public static ListNoticesResponse from(String noticeId, String title, String content,
                                          String createdBy, LocalDateTime createdAt,
                                          LocalDateTime startsAt, LocalDateTime endsAt) {
        return new ListNoticesResponse(noticeId, title, content, createdBy, createdAt, startsAt, endsAt);
    }
}