package com.acc.local.service.modules.volume.snapshot.scheduler;

import com.acc.local.dto.volume.snapshot.VolumeSnapshotRequest;
import com.acc.local.dto.volume.snapshot.VolumeSnapshotResponse;
import com.acc.local.entity.SnapshotPolicyEntity;
import com.acc.local.entity.SnapshotTaskEntity;
import com.acc.local.repository.ports.SnapshotPolicyRepositoryPort;
import com.acc.local.repository.ports.SnapshotTaskRepositoryPort;
import com.acc.local.service.modules.volume.snapshot.VolumeSnapshotModule;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 스냅샷 정책 개별 처리기
 *
 * <p>각 정책을 독립적인 트랜잭션으로 처리하여 부분 실패를 허용한다.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SnapshotPolicyProcessor {

    private static final DateTimeFormatter SNAPSHOT_NAME_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd-HH");

    private final SnapshotPolicyRepositoryPort policyRepository;
    private final SnapshotTaskRepositoryPort taskRepository;
    private final VolumeSnapshotModule volumeSnapshotModule;

    /**
     * 정책별 독립 트랜잭션으로 처리
     *
     * <p>각 정책 실패가 다른 정책에 영향을 주지 않도록 새로운 트랜잭션을 생성한다.
     *
     * @param policy 처리할 스냅샷 정책
     * @param scheduledAt 실행 시각
     * @param adminToken 시스템 관리자 토큰 (배치 작업 전체에서 재사용)
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void processPolicyWithTransaction(SnapshotPolicyEntity policy, LocalDateTime scheduledAt, String adminToken) {
        try {
            processPolicy(policy, scheduledAt, adminToken);
        } catch (Exception e) {
            // 예외를 로깅하되 전파하지 않아 다음 정책 처리를 계속한다
            log.error("Uncaught exception while processing policy. policyId={}, error={}",
                    policy.getId(), e.getMessage(), e);
        }
    }

    private void processPolicy(SnapshotPolicyEntity policy, LocalDateTime scheduledAt, String adminToken) {
        log.info("Processing snapshot policy. policyId={}, volumeId={}, projectId={}",
                policy.getId(), policy.getVolumeId(), policy.getProjectId());

        SnapshotTaskEntity task = taskRepository.save(SnapshotTaskEntity.builder()
                .policyId(policy.getId())
                .projectId(policy.getProjectId())
                .volumeId(policy.getVolumeId())
                .scheduledAt(policy.getNextRunAt())
                .policyNameAtExecution(policy.getName())
                .intervalTypeAtExecution(policy.getIntervalType())
                .build());

        task.start();
        taskRepository.save(task);

        try {
            VolumeSnapshotRequest request = new VolumeSnapshotRequest();
            request.setSourceVolumeId(policy.getVolumeId());
            request.setName(generateSnapshotName(policy, scheduledAt));

            VolumeSnapshotResponse response = volumeSnapshotModule.createSnapshot(adminToken, policy.getProjectId(), request);

            task.complete(response.getSnapshotId());
            taskRepository.save(task);

            log.info("Snapshot created successfully. policyId={}, snapshotId={}",
                    policy.getId(), response.getSnapshotId());

        } catch (Exception e) {
            // 외부 볼륨 장애 처리: task를 FAILED로 기록하고 다음 주기로 진행
            task.fail();
            taskRepository.save(task);

            log.error("Failed to create snapshot. policyId={}, volumeId={}, error={}",
                    policy.getId(), policy.getVolumeId(), e.getMessage());
        }

        // 성공/실패 여부와 무관하게 다음 실행 시간 업데이트
        policy.updateNextRunAt(calculateNextRunAt(policy));
        policyRepository.save(policy);
    }

    /**
     * 인터벌 타입에 따라 다음 실행 시각 계산
     * - DAILY  : nextRunAt + 1일
     * - WEEKLY : nextRunAt + 7일
     * - MONTHLY: nextRunAt + 1개월 (일 수 부족 시 말일로 자동 조정)
     */
    private LocalDateTime calculateNextRunAt(SnapshotPolicyEntity policy) {
        LocalDateTime base = policy.getNextRunAt();
        return switch (policy.getIntervalType()) {
            case DAILY -> base.plusDays(1);
            case WEEKLY -> base.plusWeeks(1);
            case MONTHLY -> base.plusMonths(1);
        };
    }

    /**
     * 스냅샷 이름 생성: {정책명}-{yyyyMMdd-HH}
     */
    private String generateSnapshotName(SnapshotPolicyEntity policy, LocalDateTime scheduledAt) {
        return policy.getName() + "-" + scheduledAt.format(SNAPSHOT_NAME_FORMATTER);
    }
}