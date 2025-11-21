package com.acc.local.repository.ports;

import com.acc.local.entity.SnapshotPolicyEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface SnapshotPolicyRepositoryPort {
    SnapshotPolicyEntity save(SnapshotPolicyEntity entity);
    Optional<SnapshotPolicyEntity> findById(Long id);
    Page<SnapshotPolicyEntity> findAll(Pageable pageable);
    Page<SnapshotPolicyEntity> findByVolumeId(String volumeId, Pageable pageable);
    void deleteById(Long id);
}

