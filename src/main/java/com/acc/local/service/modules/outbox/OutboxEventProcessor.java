package com.acc.local.service.modules.outbox;

import com.acc.local.entity.OutboxEventEntity;
import com.acc.local.repository.jpa.OutboxEventJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class OutboxEventProcessor {

    private final OutboxEventJpaRepository outboxEventJpaRepository;

    /**
     * 처리되지 않은 이벤트를 조회하고 처리
     */
    @Transactional
    public void processUnprocessedEvents() {
        List<OutboxEventEntity> unprocessedEvents = outboxEventJpaRepository.findByProcessedFalseOrderByCreatedAtAsc();

        log.info("처리되지 않은 Outbox 이벤트 {}개", unprocessedEvents.size());

        for (OutboxEventEntity event : unprocessedEvents) {
            try {
                // TODO: 외부 시스템으로 이벤트를 전송하는 로직 추가

                log.info("Outbox 이벤트 처리 중: eventId={}, aggregateType={}, aggregateId={}",
                        event.getEventId(), event.getAggregateType(), event.getAggregateId());

                // 처리 완료 표시
                event.markAsProcessed();
                outboxEventJpaRepository.save(event);
            } catch (Exception e) {
                log.error("Outbox 이벤트 처리 실패: eventId={}", event.getEventId(), e);
                // 실패한 이벤트는 다음 처리 사이클에서 재시도
            }
        }
    }
}
