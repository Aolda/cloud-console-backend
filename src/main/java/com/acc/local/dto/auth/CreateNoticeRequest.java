package com.acc.local.dto.auth;

import lombok.Builder;

@Builder
public record CreateNoticeRequest(
        String title,
        String content,
        String startsAt,
        String endsAt
) {
}