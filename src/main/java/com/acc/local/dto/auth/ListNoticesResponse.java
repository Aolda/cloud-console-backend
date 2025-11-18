package com.acc.local.dto.auth;

import java.time.LocalDateTime;

public record ListNoticesResponse(
        String noticeId,
        String title,
        String content,
        String createdBy,
        LocalDateTime createdAt,
        LocalDateTime startsAt,
        LocalDateTime endsAt
) {
    public static ListNoticesResponse from(String noticeId, String title, String content,
                                          String createdBy, LocalDateTime createdAt,
                                          LocalDateTime startsAt, LocalDateTime endsAt) {
        return new ListNoticesResponse(noticeId, title, content, createdBy, createdAt, startsAt, endsAt);
    }
}