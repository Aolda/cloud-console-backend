package com.acc.local.repository.adapters;

import com.acc.global.common.PageResponse;
import com.acc.local.dto.keypair.KeypairListResponse;
import com.acc.local.entity.KeypairEntity;
import com.acc.local.entity.id.KeypairProjectId;
import com.acc.local.repository.jpa.KeypairJpaRepository;
import com.acc.local.repository.modules.KeypairQueryDSLModule;
import com.acc.local.repository.ports.KeypairRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class KeypairRepositoryAdapter implements KeypairRepositoryPort {

    private final KeypairJpaRepository keypairJpaRepository;
    private final KeypairQueryDSLModule keypairQueryDSLModule;

    @Override
    public Optional<KeypairEntity> findById(KeypairProjectId keypairProjectId) {
        return keypairJpaRepository.findById(keypairProjectId);
    }

    @Override
    public void save(KeypairEntity keypairEntity) {
        keypairJpaRepository.save(keypairEntity);
    }

    @Override
    public void delete(KeypairEntity keypair) {
        keypairJpaRepository.delete(keypair);
    }

    @Override
    public PageResponse<KeypairListResponse> findKeypairsByProjectId(String projectId, String marker, String direction, int limit) {
        return keypairQueryDSLModule.findKeypairsByProjectId(projectId, marker, direction, limit);
    }

    @Override
    public List<KeypairEntity> findAllByProjectId(String projectId) {
        return keypairQueryDSLModule.findAllByProjectId(projectId);
    }

    @Override
    public void saveAll(List<KeypairEntity> keypairs) {
        keypairJpaRepository.saveAll(keypairs);
    }

    @Override
    public void deleteAll(List<KeypairEntity> keypairs) {
        keypairJpaRepository.deleteAll(keypairs);
    }
}
