package com.acc.local.dto.auth;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record CreateNoticeRequest(

    @Schema(description = "공지의 제목", example = "장학 공지", requiredMode = Schema.RequiredMode.REQUIRED)
    String title,

    @Schema(description = "공지의 내용", example = "아올다 회원은 전액 장학을 지원합니다.", requiredMode = Schema.RequiredMode.REQUIRED)
    String content,

    @Schema(
        description = "공지 게시 시간 (ISO-8601, 초 단위, KST, 타임존 미포함)",
        example = "2025-01-01T00:00:00",
        type = "string",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    LocalDateTime startsAt,

    @Schema(
        description = "공지 종료 시간 (ISO-8601, 초 단위, KST, 타임존 미포함)",
        example = "2025-12-31T23:59:59",
        type = "string",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    LocalDateTime endsAt
) {
}
