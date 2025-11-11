package com.acc.local.repository.jpa;

import com.acc.local.entity.KeypairEntity;
import com.acc.local.entity.KeypairProjectId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface KeypairJpaRepository extends JpaRepository<KeypairEntity, KeypairProjectId> {

    Optional<KeypairEntity> findByKeypairIdAndProjectProjectId(String keypairId, String projectId);
}
