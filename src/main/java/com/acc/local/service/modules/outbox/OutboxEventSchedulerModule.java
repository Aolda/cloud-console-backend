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

    @Scheduled(fixedDelay = 600000) // 10분
    @SchedulerLock(
            name = "OutboxEventProcessor_processPendingEvents",
            lockAtMostFor = "9m",
            lockAtLeastFor = "1m"
    )
    public void processPendingEvents() {
        List<OutboxEventEntity> pendingEvents = outboxMessageRepositoryPort.findPendingEvents();

        if (pendingEvents.isEmpty()) {
            return;
        }

        log.info("Processing {} pending outbox events", pendingEvents.size());

        for (OutboxEventEntity event : pendingEvents) {
            try {
                outboxEventProcessorModule.processEvent(event);
            } catch (Exception e) {
                log.error("Failed to process outbox event: eventId={}, aggregateId={}",
                        event.getEventId(), event.getAggregateId(), e);
            }
        }
    }
}
