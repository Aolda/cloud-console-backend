package com.acc.local.repository.jpa;

import com.acc.local.entity.SnapshotPolicyEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface SnapshotPolicyJpaRepository extends JpaRepository<SnapshotPolicyEntity, Long> {
    Page<SnapshotPolicyEntity> findAll(Pageable pageable);
    Optional<SnapshotPolicyEntity> findById(Long id);
    Page<SnapshotPolicyEntity> findByProjectId(String projectId, Pageable pageable);
    Optional<SnapshotPolicyEntity> findByIdAndProjectId(Long id, String projectId);
    Page<SnapshotPolicyEntity> findByVolumeId(String volumeId, Pageable pageable);

    @Query("""
    select p from SnapshotPolicyEntity p
    where p.enabled = true
      and p.nextRunAt > :from
      and p.nextRunAt <= :now
      and (p.expirationDate is null or p.expirationDate > :now)
    """)
    Page<SnapshotPolicyEntity> findDuePolicies(@Param("from") LocalDateTime from, @Param("now") LocalDateTime now, Pageable pageable);
}