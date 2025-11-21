package com.acc.local.repository.adapters;

import com.acc.local.entity.SnapshotPolicyEntity;
import com.acc.local.repository.jpa.SnapshotPolicyJpaRepository;
import com.acc.local.repository.ports.SnapshotPolicyRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Primary
@RequiredArgsConstructor
public class SnapshotPolicyRepositoryAdapter implements SnapshotPolicyRepositoryPort {

    private final SnapshotPolicyJpaRepository jpaRepository;

    @Override
    public SnapshotPolicyEntity save(SnapshotPolicyEntity entity) {
        return jpaRepository.save(entity);
    }

    @Override
    public Optional<SnapshotPolicyEntity> findById(Long id) {
        return jpaRepository.findById(id);
    }

    @Override
    public Page<SnapshotPolicyEntity> findAll(Pageable pageable) {
        return jpaRepository.findAll(pageable);
    }

    @Override
    public Page<SnapshotPolicyEntity> findByVolumeId(String volumeId, Pageable pageable) {
        return jpaRepository.findByVolumeId(volumeId, pageable);
    }

    @Override
    public void deleteById(Long id) {
        jpaRepository.deleteById(id);
    }
}

