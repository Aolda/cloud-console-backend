package com.acc.local.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;

public record NoticeFilterRequest(

    @Schema(
        description = "활성 공지만 조회 여부\n"
                + "- true: 현재 시간 기준 활성 공지만 (startsAt <= now <= endsAt)\n"
                + "- false or null: 전체 공지 조회",
        example = "true"
    )
    Boolean activeOnly
) {
    // 기본값 처리를 위한 정적 팩토리 메서드
    public static NoticeFilterRequest of(Boolean activeOnly) {
        return new NoticeFilterRequest(activeOnly != null ? activeOnly : false);
    }
}
