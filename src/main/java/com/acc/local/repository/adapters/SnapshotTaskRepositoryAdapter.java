package com.acc.local.repository.adapters;

import com.acc.local.domain.enums.TaskStatus;
import com.acc.local.entity.SnapshotTaskEntity;
import com.acc.local.repository.jpa.SnapshotTaskJpaRepository;
import com.acc.local.repository.ports.SnapshotTaskRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Primary
@RequiredArgsConstructor
public class SnapshotTaskRepositoryAdapter implements SnapshotTaskRepositoryPort {

    private final SnapshotTaskJpaRepository jpaRepository;

    @Override
    public SnapshotTaskEntity save(SnapshotTaskEntity entity) {
        return jpaRepository.save(entity);
    }

    @Override
    public Page<SnapshotTaskEntity> findByPolicyId(Long policyId, Pageable pageable) {
        return jpaRepository.findByPolicyId(policyId, pageable);
    }

    @Override
    public Page<SnapshotTaskEntity> findByPolicyIdAndStartedAtAfter(Long policyId, LocalDateTime since, Pageable pageable) {
        return jpaRepository.findByPolicyIdAndStartedAtAfter(policyId, since, pageable);
    }

    @Override
    public List<SnapshotTaskEntity> findByPolicyIdAndStatus(Long policyId, TaskStatus status) {
        return jpaRepository.findByPolicyIdAndStatus(policyId, status);
    }
}

