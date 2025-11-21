package com.acc.local.repository.ports;

import com.acc.local.domain.enums.TaskStatus;
import com.acc.local.entity.SnapshotTaskEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

public interface SnapshotTaskRepositoryPort {
    SnapshotTaskEntity save(SnapshotTaskEntity entity);
    Page<SnapshotTaskEntity> findByPolicyId(Long policyId, Pageable pageable);
    Page<SnapshotTaskEntity> findByPolicyIdAndStartedAtAfter(Long policyId, LocalDateTime since, Pageable pageable);
    List<SnapshotTaskEntity> findByPolicyIdAndStatus(Long policyId, TaskStatus status);
}

