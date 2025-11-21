package com.acc.local.entity;

import com.acc.local.domain.enums.IntervalType;
import com.acc.local.domain.enums.TaskStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 스냅샷 작업 실행 이력 엔티티
 */
@Entity
@Table(name = "snapshot_tasks")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SnapshotTaskEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "policy_id", nullable = false)
    private Long policyId;

    @Column(name = "volume_id", length = 64, nullable = false)
    private String volumeId;

    @Column(name = "snapshot_id", length = 64, nullable = false)
    private String snapshotId;

    @Column(name = "policy_name_at_execution", length = 100)
    private String policyNameAtExecution;

    @Enumerated(EnumType.STRING)
    @Column(name = "interval_type_at_execution", length = 20)
    private IntervalType intervalTypeAtExecution;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private TaskStatus status;

    @Column(name = "started_at")
    private LocalDateTime startedAt;

    @Column(name = "finished_at")
    private LocalDateTime finishedAt;

    @PrePersist
    protected void onCreate() {
        if (this.status == null) {
            this.status = TaskStatus.PENDING;
        }
    }

    @Builder
    public SnapshotTaskEntity(Long policyId, String volumeId, String snapshotId,
                             String policyNameAtExecution, IntervalType intervalTypeAtExecution,
                             TaskStatus status, LocalDateTime startedAt, LocalDateTime finishedAt) {
        this.policyId = policyId;
        this.volumeId = volumeId;
        this.snapshotId = snapshotId;
        this.policyNameAtExecution = policyNameAtExecution;
        this.intervalTypeAtExecution = intervalTypeAtExecution;
        this.status = status;
        this.startedAt = startedAt;
        this.finishedAt = finishedAt;
    }

    public void start() {
        this.status = TaskStatus.RUNNING;
        this.startedAt = LocalDateTime.now();
    }

    public void complete() {
        this.status = TaskStatus.SUCCESS;
        this.finishedAt = LocalDateTime.now();
    }

    public void fail() {
        this.status = TaskStatus.FAILED;
        this.finishedAt = LocalDateTime.now();
    }
}

