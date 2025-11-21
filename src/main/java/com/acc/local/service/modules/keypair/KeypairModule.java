package com.acc.local.service.modules.keypair;

import com.acc.global.common.PageResponse;
import com.acc.global.exception.auth.KeystoneException;
import com.acc.global.exception.keypair.KeypairErrorCode;
import com.acc.global.exception.keypair.KeypairException;
import com.acc.global.exception.keypair.KeypairExternalErrorCode;
import com.acc.global.exception.keypair.KeypairExternalException;
import com.acc.local.dto.keypair.CreateKeypairRequest;
import com.acc.local.dto.keypair.CreateKeypairResponse;
import com.acc.local.dto.keypair.KeypairListResponse;
import com.acc.local.entity.KeypairEntity;
import com.acc.local.entity.KeypairProjectId;
import com.acc.local.entity.ProjectEntity;
import com.acc.local.external.ports.KeypairExternalPort;
import com.acc.local.repository.ports.KeypairRepositoryPort;
import com.acc.local.repository.ports.ProjectRepositoryPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class KeypairModule {

    private final KeypairRepositoryPort keypairRepositoryPort;
    private final ProjectRepositoryPort projectRepositoryPort;
    private final KeypairExternalPort keypairExternalPort;

    public PageResponse<KeypairListResponse> getKeypairs(String projectId, String marker, String direction, int limit) {
        return keypairRepositoryPort.findKeypairsByProjectId(projectId, marker, direction, limit);
    }

    @Transactional
    public CreateKeypairResponse createKeypair(CreateKeypairRequest request, String keystoneToken, String projectId) {
        CreateKeypairResponse response = keypairExternalPort.createKeypair(keystoneToken, request);

        // DB 에서 프로젝트 존재 여부를 먼저 확인
        ProjectEntity project = projectRepositoryPort.findById(projectId)
                .orElseThrow(() -> new KeystoneException(KeypairErrorCode.DB_PROJECT_NOT_FOUND));

        try {
            KeypairEntity keypairEntity = KeypairEntity.builder()
                    .keypairId(response.getFingerprint())
                    .keypairName(response.getKeypairName())
                    .project(project)
                    .build();
            keypairRepositoryPort.save(keypairEntity);
            log.info("Successfully created keypair.");
            return response;
        } catch (Exception e) {
            // DB 저장 실패 시 OpenStack에 생성된 리소스 롤백
            log.warn("Failed to save keypair to DB. Rolling back OpenStack creation for keypair: {}", response.getKeypairName(), e);
            try {
                keypairExternalPort.deleteKeypair(keystoneToken, response.getKeypairName());
                log.warn("Roll Back Success (Failed to save keypair): {}", response.getKeypairName());
            } catch (Exception rollbackEx) {
                log.error("CRITICAL: Failed to rollback OpenStack keypair creation: {}. Orphan resource.", response.getKeypairName(), rollbackEx);
            }
            throw new KeypairException(KeypairErrorCode.DB_SAVE_FAILED);
        }
    }

    @Transactional
    public void deleteKeypair(String keypairId, String keystoneToken, String projectId) {
        KeypairProjectId keypairProjectId = new KeypairProjectId(keypairId, projectId);
        KeypairEntity keypair = keypairRepositoryPort.findById(keypairProjectId)
                .orElseThrow(() -> new KeypairException(KeypairErrorCode.DB_KEYPAIR_NOT_FOUND));

        // DB 삭제 (트랜잭션 롤백 대상)
        keypairRepositoryPort.delete(keypair);

        // OpenStack API 삭제 시도
        try {
            keypairExternalPort.deleteKeypair(keystoneToken, keypair.getKeypairName());
            log.info("Successfully deleted keypair from OpenStack and DB. KeypairName:{}", keypair.getKeypairName());

        } catch (KeypairExternalException ex) {
            if (ex.getErrorCode() == KeypairExternalErrorCode.KEYPAIR_EXTERNAL_NOT_FOUND) {
                // 404(이미 없음)는 성공으로 간주
                log.warn("Keypair '{}' already deleted from OpenStack.", keypair.getKeypairName());
            } else {
                log.error("Failed to delete keypair from OpenStack (Error: {}). Rolling back DB deletion.", ex.getErrorCode().getMessage(), ex);
                throw ex;  // Transaction Rollback
            }
        }
    }
}
