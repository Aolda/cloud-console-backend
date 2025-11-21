package com.acc.local.repository.jpa;

import com.acc.local.domain.enums.TaskStatus;
import com.acc.local.entity.SnapshotTaskEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface SnapshotTaskJpaRepository extends JpaRepository<SnapshotTaskEntity, Long> {
    Page<SnapshotTaskEntity> findByPolicyId(Long policyId, Pageable pageable);
    
    @Query("SELECT t FROM SnapshotTaskEntity t WHERE t.policyId = :policyId AND t.startedAt >= :since")
    Page<SnapshotTaskEntity> findByPolicyIdAndStartedAtAfter(
            @Param("policyId") Long policyId,
            @Param("since") LocalDateTime since,
            Pageable pageable
    );
    
    List<SnapshotTaskEntity> findByPolicyIdAndStatus(Long policyId, TaskStatus status);
}

