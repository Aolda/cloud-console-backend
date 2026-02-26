package com.acc.local.service.modules.outbox;

import com.acc.local.entity.OutboxEventEntity;
import com.acc.local.repository.ports.OutboxMessageRepositoryPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Outbox 패턴 이벤트 스케줄러
 * 주기적으로 처리되지 않은 이벤트를 조회하여 처리합니다.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class OutboxEventSchedulerModule {

    private final OutboxMessageRepositoryPort outboxMessageRepositoryPort;
    private final OutboxEventProcessorModule outboxEventProcessorModule;

    /**
     * 10초마다 미처리 이벤트를 확인하고 처리
     * ShedLock으로 분산 환경에서 중복 실행 방지
     * 각 이벤트는 독립적인 트랜잭션으로 처리
     */
    @Scheduled(fixedDelay = 10000)
    @SchedulerLock(
            name = "OutboxEventProcessor_processUnprocessedEvents",
            lockAtMostFor = "15s",
            lockAtLeastFor = "5s"
    )
    public void processUnprocessedEvents() {
        List<OutboxEventEntity> unprocessedEvents = outboxMessageRepositoryPort.findByProcessedFalse();

        if (unprocessedEvents.isEmpty()) {
            return;
        }

        log.info("Processing {} unprocessed outbox events", unprocessedEvents.size());

        for (OutboxEventEntity event : unprocessedEvents) {
            try {
                outboxEventProcessorModule.processEvent(event);
            } catch (Exception e) {
                log.error("Failed to process outbox event: eventId={}, aggregateId={}",
                        event.getEventId(), event.getAggregateId(), e);
            }
        }
    }
}
