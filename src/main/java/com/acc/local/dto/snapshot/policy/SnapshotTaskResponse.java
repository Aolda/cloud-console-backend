package com.acc.local.dto.snapshot.policy;

import com.acc.local.domain.enums.IntervalType;
import com.acc.local.domain.enums.TaskStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@Schema(description = "스냅샷 작업 실행 이력 응답")
public class SnapshotTaskResponse {

    @Schema(description = "작업 ID", example = "1")
    private Long taskId;

    @Schema(description = "정책 ID", example = "1")
    private Long policyId;

    @Schema(description = "볼륨 ID", example = "a1b2c3d4-e5f6-7890-abcd-ef1234567890")
    private String volumeId;

    @Schema(description = "스냅샷 ID", example = "e1f2a3b4-c5d6-e7f8-a9b0-c1d2e3f4a5b6")
    private String snapshotId;

    @Schema(description = "실행 시점의 정책 이름", example = "daily-backup-policy")
    private String policyNameAtExecution;

    @Schema(description = "실행 시점의 간격 타입", example = "DAILY")
    private IntervalType intervalTypeAtExecution;

    @Schema(description = "작업 상태", example = "SUCCESS")
    private TaskStatus status;

    @Schema(description = "시작 일시", example = "2025-01-01T02:00:00")
    private LocalDateTime startedAt;

    @Schema(description = "완료 일시", example = "2025-01-01T02:05:00")
    private LocalDateTime finishedAt;
}

