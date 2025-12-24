package com.acc.local.dto.snapshot.policy;

import com.acc.local.domain.enums.IntervalType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@Schema(description = "스냅샷 수명관리자 정책 생성/수정 요청")
public class SnapshotPolicyRequest {

    @Schema(description = "정책 이름", example = "daily-backup-policy", required = true)
    private String name;

    @Schema(description = "정책 설명", example = "매일 자동 백업 정책")
    private String description;

    @Schema(description = "볼륨 ID", example = "a1b2c3d4-e5f6-7890-abcd-ef1234567890", required = true)
    private String volumeId;

    @Schema(description = "간격 타입 (DAILY, WEEKLY, MONTHLY)", example = "DAILY", required = true)
    private IntervalType intervalType;

    @Schema(description = "만료 일시", example = "2025-12-31T23:59:59")
    private LocalDateTime expirationDate;

    @Schema(description = "일간 실행 시간 (intervalType이 DAILY인 경우)", example = "02:00:00")
    private LocalTime dailyTime;

    @Schema(description = "주간 요일 (0=일요일, 1=월요일, ..., 6=토요일, intervalType이 WEEKLY인 경우)", example = "1")
    private Integer weeklyDayOfWeek;

    @Schema(description = "주간 실행 시간 (intervalType이 WEEKLY인 경우)", example = "02:00:00")
    private LocalTime weeklyTime;

    @Schema(description = "월간 일자 (1-31, intervalType이 MONTHLY인 경우)", example = "1")
    private Integer monthlyDayOfMonth;

    @Schema(description = "월간 실행 시간 (intervalType이 MONTHLY인 경우)", example = "02:00:00")
    private LocalTime monthlyTime;

    @Schema(description = "타임존", example = "Asia/Seoul")
    private String timezone;
}

