package com.acc.local.service.modules.keypair;

import com.acc.local.dto.keypair.KeypairSyncDto;
import com.acc.local.entity.KeypairEntity;
import com.acc.local.entity.ProjectEntity;
import com.acc.local.entity.UserDbExtraEntity;
import com.acc.local.external.ports.KeypairExternalPort;
import com.acc.local.repository.ports.KeypairRepositoryPort;
import com.acc.local.repository.ports.ProjectRepositoryPort;
import com.acc.local.repository.ports.UserRepositoryPort;
import com.acc.local.service.modules.auth.AuthModule;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * OpenStack과 DB 간의 Keypair 동기화를 담당하는 모듈
 * - userId 기반으로 모든 사용자의 전체 keypair를 조회하여 fingerprint 기준으로 동기화
 * - Case 1: OpenStack에 없고 DB에 있는 경우 → DB에서 삭제 (실제 존재하지 않는 리소스)
 * - Case 2: Fingerprint는 같지만 Name이 다른 경우 → OpenStack의 이름을 기준으로 DB에서 업데이트
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class KeypairSyncModule {

    private final KeypairRepositoryPort keypairRepositoryPort;
    private final ProjectRepositoryPort projectRepositoryPort;
    private final UserRepositoryPort userRepositoryPort;
    private final KeypairExternalPort keypairExternalPort;
    private final AuthModule authModule;

    /**
     * 전체 시스템의 Keypair 동기화 수행
     * 매일 새벽 4시에 실행 (cron: 초 분 시 일 월 요일)
     */
    @Scheduled(cron = "0 0 4 * * *")
    @SchedulerLock(name = "keypairSync", lockAtMostFor = "2h", lockAtLeastFor = "30m")
    @Transactional
    public void syncAllKeypairs() {
        log.info("Starting global keypair synchronization");

        try {
            // 1. DB에서 모든 keypair 조회 (fingerprint 기준)
            Map<String, KeypairEntity> dbKeypairMap = getAllDbKeypairs();

            // 2. DB에 저장된 모든 고유 userId 추출
            Set<String> allUserIds = dbKeypairMap.values().stream()
                    .map(k -> k.getUser().getUserId())
                    .collect(Collectors.toSet());

            // 3. OpenStack에서 각 사용자의 모든 keypair 조회 (fingerprint 기준)
            Map<String, KeypairSyncDto> openstackKeypairMap = getAllOpenstackKeypairs(allUserIds);

            // 4. 동기화 수행
            performSync(dbKeypairMap, openstackKeypairMap);

            log.info("Completed global keypair synchronization");
        } catch (Exception e) {
            log.error("Critical error during keypair synchronization", e);
        }
    }

    /**
     * DB에서 모든 Keypair를 조회하여 fingerprint를 키로 하는 맵 생성
     */
    private Map<String, KeypairEntity> getAllDbKeypairs() {
        List<KeypairEntity> allKeypairs = new ArrayList<>();

        // 모든 프로젝트에서 keypair 조회
        List<ProjectEntity> allProjects = projectRepositoryPort.findAll();
        for (ProjectEntity project : allProjects) {
            try {
                List<KeypairEntity> projectKeypairs = keypairRepositoryPort.findAllByProjectId(project.getProjectId());
                allKeypairs.addAll(projectKeypairs);
            } catch (Exception e) {
                log.warn("Failed to fetch keypairs for project: {}. Error: {}",
                        project.getProjectId(), e.getMessage());
            }
        }

        return allKeypairs.stream()
                .collect(Collectors.toMap(
                        KeypairEntity::getKeypairId,  // fingerprint
                        k -> k,
                        (existing, replacement) -> existing  // 중복 시 기존 값 유지
                ));
    }

    /**
     * OpenStack에서 모든 사용자의 Keypair를 조회하여 fingerprint를 키로 하는 맵 생성
     */
    private Map<String, KeypairSyncDto> getAllOpenstackKeypairs(Set<String> allUserIds) {
        Map<String, KeypairSyncDto> openstackKeypairMap = new HashMap<>();

        for (String userId : allUserIds) {
            try {
                // 사용자 정보 조회
                UserDbExtraEntity user = userRepositoryPort.findUserDetailById(userId).orElse(null);
                if (user == null) {
                    log.warn("User not found in DB: {}", userId);
                    continue;
                }

                // Unscoped 토큰 발급하여 해당 사용자의 모든 keypair 조회
                String unscopedToken = authModule.getUnscopedTokenByUserId(userId);
                List<KeypairSyncDto> userKeypairs = keypairExternalPort.listKeypairsByUser(unscopedToken);

                for (KeypairSyncDto osKeypair : userKeypairs) {
                    // userId 정보 추가 (해당 사용자로 조회했으므로)
                    KeypairSyncDto enrichedKeypair = osKeypair.toBuilder()
                            .userId(userId)  // 조회한 사용자의 ID 설정
                            .build();
                    openstackKeypairMap.put(enrichedKeypair.getFingerprint(), enrichedKeypair);
                }
            } catch (Exception e) {
                log.warn("Failed to fetch keypairs for user: {}. Error: {}", userId, e.getMessage());
            }
        }

        return openstackKeypairMap;
    }

    /**
     * DB와 OpenStack의 Keypair 동기화 수행
     */
    private void performSync(Map<String, KeypairEntity> dbKeypairMap, Map<String, KeypairSyncDto> openstackKeypairMap) {

        List<KeypairEntity> toDelete = new ArrayList<>();
        int updatedCount = 0;

        // Case 1: OpenStack에 없고 DB에 있는 경우 → DB에서 삭제
        for (Map.Entry<String, KeypairEntity> entry : dbKeypairMap.entrySet()) {
            String fingerprint = entry.getKey();
            KeypairEntity dbKeypair = entry.getValue();

            if (!openstackKeypairMap.containsKey(fingerprint)) {
                toDelete.add(dbKeypair);
                log.info("Case 1 - Marking for deletion from DB: fingerprint={}, name={}",
                        fingerprint, dbKeypair.getKeypairName());
            } else {
                // Case 2: Fingerprint는 같지만 Name이 다른 경우 → DB 업데이트
                KeypairSyncDto osKeypair = openstackKeypairMap.get(fingerprint);
                if (!dbKeypair.getKeypairName().equals(osKeypair.getName())) {
                    String oldName = dbKeypair.getKeypairName();
                    dbKeypair.updateKeypairName(osKeypair.getName());
                    updatedCount++;
                    log.info("Case 2 - Updating keypair name in DB: fingerprint={}, oldName={}, newName={}",
                            fingerprint, oldName, osKeypair.getName());
                }
            }
        }

        // DB 변경사항 일괄 처리 (삭제만 명시적 처리, 업데이트는 Dirty Checking으로 자동 처리)
        if (!toDelete.isEmpty()) {
            keypairRepositoryPort.deleteAll(toDelete);
            log.info("Deleted {} keypairs from DB", toDelete.size());
        }
        log.info("Sync completed. Deleted: {}, Updated: {}", toDelete.size(), updatedCount);
    }
}


