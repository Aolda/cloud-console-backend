package com.acc.local.dto.snapshot.policy;

import com.acc.local.domain.enums.IntervalType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Getter
@Setter
@Builder
@Schema(description = "스냅샷 수명관리자 정책 응답")
public class SnapshotPolicyResponse {

    @Schema(description = "정책 ID", example = "1")
    private Long policyId;

    @Schema(description = "정책 이름", example = "daily-backup-policy")
    private String name;

    @Schema(description = "정책 설명", example = "매일 자동 백업 정책")
    private String description;

    @Schema(description = "볼륨 ID", example = "a1b2c3d4-e5f6-7890-abcd-ef1234567890")
    private String volumeId;

    @Schema(description = "간격 타입", example = "DAILY")
    private IntervalType intervalType;

    @Schema(description = "활성화 여부", example = "true")
    private Boolean enabled;

    @Schema(description = "만료 일시", example = "2025-12-31T23:59:59")
    private LocalDateTime expirationDate;

    @Schema(description = "생성 일시", example = "2025-01-01T00:00:00")
    private LocalDateTime createdAt;

    @Schema(description = "수정 일시", example = "2025-01-02T00:00:00")
    private LocalDateTime updatedAt;

    @Schema(description = "일간 실행 시간", example = "02:00:00")
    private LocalTime dailyTime;

    @Schema(description = "주간 요일", example = "1")
    private Integer weeklyDayOfWeek;

    @Schema(description = "주간 실행 시간", example = "02:00:00")
    private LocalTime weeklyTime;

    @Schema(description = "월간 일자", example = "1")
    private Integer monthlyDayOfMonth;

    @Schema(description = "월간 실행 시간", example = "02:00:00")
    private LocalTime monthlyTime;

    @Schema(description = "타임존", example = "Asia/Seoul")
    private String timezone;
}

