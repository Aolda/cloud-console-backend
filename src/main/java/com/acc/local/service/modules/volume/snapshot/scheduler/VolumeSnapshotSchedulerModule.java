package com.acc.local.service.modules.volume.snapshot.scheduler;

import com.acc.local.entity.SnapshotPolicyEntity;
import com.acc.local.repository.ports.SnapshotPolicyRepositoryPort;
import com.acc.local.service.modules.auth.AuthModule;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * 볼륨 스냅샷 스케줄러 모듈
 *
 * <p>배치 작업 전체를 오케스트레이션하고 토큰 생명주기를 관리한다.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class VolumeSnapshotSchedulerModule {

    private static final int PAGE_SIZE = 100;

    private final SnapshotPolicyRepositoryPort policyRepository;
    private final AuthModule authModule;
    private final SnapshotPolicyProcessor policyProcessor;
    /**
     * 시간 윈도우 내 실행 대상 정책을 페이징으로 순회하며 스냅샷 생성
     *
     * <p>각 정책은 독립적인 트랜잭션으로 처리되어 부분 실패를 허용한다.
     * <p>시스템 관리자 토큰은 배치 작업 전체에서 재사용되며, 완료 후 즉시 폐기된다.
     *
     * @param from 윈도우 시작 (이전 정각)
     * @param now  윈도우 종료 (현재 정각)
     */
    public void processScheduledPolicies(LocalDateTime from, LocalDateTime now) {
        // 배치 작업 전체에서 사용할 시스템 관리자 토큰 발급
        String adminToken = authModule.issueSystemAdminTokenWithAdminProjectScope("[ACC server]: snapshot scheduler");
        log.info("Issued system admin token for snapshot scheduler batch");

        try {
            int page = 0;
            Page<SnapshotPolicyEntity> policies;

            do {
                policies = policyRepository.findDuePolicies(from, now, PageRequest.of(page++, PAGE_SIZE));
                log.info("Processing snapshot policies. window={} ~ {}, page={}, total={}",
                        from, now, page - 1, policies.getTotalElements());

                for (SnapshotPolicyEntity policy : policies.getContent()) {
                    // 별도 클래스로 분리하여 자연스럽게 프록시 적용
                    policyProcessor.processPolicyWithTransaction(policy, now, adminToken);
                }
            } while (policies.hasNext());

        } finally {
            // 배치 작업 완료 후 토큰 즉시 폐기
            try {
                authModule.invalidateSystemAdminToken(adminToken);
                log.info("Revoked system admin token for snapshot scheduler batch");
            } catch (Exception e) {
                log.error("Failed to revoke system admin token. token may remain active", e);
            }
        }
    }
}