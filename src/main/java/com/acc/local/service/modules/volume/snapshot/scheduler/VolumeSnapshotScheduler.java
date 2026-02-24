package com.acc.local.service.modules.volume.snapshot.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

/**
 * 볼륨 스냅샷 정책 스케줄러
 *
 * <p>매 정각마다 실행하여 이전 1시간 이내 nextRunAt이 도래한 정책을 처리한다.
 * ShedLock으로 다중 서버 환경에서의 중복 실행을 방지한다.
 *
 * <p>윈도우 방식 채택 이유:
 * - dailyTime이 시간 단위(HH:00)이므로 분 단위 폴링 불필요
 * - DB 클락 스큐 및 스케줄러 지연에 대한 내성 확보
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class VolumeSnapshotScheduler {

    private final VolumeSnapshotSchedulerModule schedulerModule;

    /**
     * 매 정각 실행 (cron: 초 분 시 일 월 요일)
     * lockAtMostFor = "50m": 다음 정각 실행 전 반드시 락 해제
     *
     * [임시 테스트] 1분마다 실행, 2분 윈도우
     */
    @Scheduled(cron = "0 * * * * *")
    @SchedulerLock(name = "volumeSnapshotScheduler", lockAtMostFor = "50s", lockAtLeastFor = "10s")
    public void executeScheduledSnapshots() {
        LocalDateTime now = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES);
        LocalDateTime from = now.minusMinutes(2);

        log.info("Volume snapshot scheduler triggered. window={} ~ {}", from, now);

        try {
            schedulerModule.processScheduledPolicies(from, now);
            log.info("Volume snapshot scheduler completed. window={} ~ {}", from, now);
        } catch (Exception e) {
            log.error("Volume snapshot scheduler failed. window={} ~ {}, error={}", from, now, e.getMessage(), e);
        }
    }
}
