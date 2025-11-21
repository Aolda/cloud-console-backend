package com.acc.local.repository.jpa;

import com.acc.local.entity.SnapshotPolicyEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SnapshotPolicyJpaRepository extends JpaRepository<SnapshotPolicyEntity, Long> {
    Page<SnapshotPolicyEntity> findAll(Pageable pageable);
    Optional<SnapshotPolicyEntity> findById(Long id);
    Page<SnapshotPolicyEntity> findByVolumeId(String volumeId, Pageable pageable);
}

