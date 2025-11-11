package com.acc.local.repository.ports;

import com.acc.global.common.PageResponse;
import com.acc.local.dto.keypair.KeypairListResponse;
import com.acc.local.entity.KeypairEntity;
import com.acc.local.entity.KeypairProjectId;

import java.util.Optional;

public interface KeypairRepositoryPort {

    Optional<KeypairEntity> findById(KeypairProjectId keypairProjectId);
    void save(KeypairEntity keypairEntity);
    void delete(KeypairEntity keypair);
    PageResponse<KeypairListResponse> findKeypairsByProjectId(String projectId, String marker, String direction, int limit);
}
