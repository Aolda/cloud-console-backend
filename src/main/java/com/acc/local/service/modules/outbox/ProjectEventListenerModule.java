package com.acc.local.service.modules.outbox;

import com.acc.local.domain.enums.outbox.AggregateType;
import com.acc.local.domain.enums.outbox.EventType;
import com.acc.local.entity.OutboxEventEntity;
import com.acc.local.repository.jpa.OutboxEventJpaRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProjectEventListenerModule {

    private final OutboxEventJpaRepository outboxEventJpaRepository;
    private final ObjectMapper objectMapper;

    /**
     * 프로젝트 요청 생성 이벤트 처리
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleProjectRequestEvent(ProjectRequestEvent event) {
        try {
            String payload = objectMapper.writeValueAsString(event);

            OutboxEventEntity outboxEvent = OutboxEventEntity.builder()
                    .aggregateType(AggregateType.PROJECT_REQUEST)
                    .aggregateId(event.projectRequestId())
                    .eventType(EventType.PROJECT_REQUEST_CREATED)
                    .payload(payload)
                    .processed(false)
                    .build();

            outboxEventJpaRepository.save(outboxEvent);
        } catch (Exception e) {
            log.error("Failed to save outbox event for projectRequestId={}", event.projectRequestId(), e);
        }
    }

    /**
     * 프로젝트 요청 결정 (승인/거부) 이벤트 처리
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleProjectRequestDecisionEvent(ProjectRequestDecisionEvent event) {
        try {
            String payload = objectMapper.writeValueAsString(event);

            OutboxEventEntity outboxEvent = OutboxEventEntity.builder()
                    .aggregateType(AggregateType.PROJECT_REQUEST)
                    .aggregateId(event.projectRequestId())
                    .eventType(event.toEventType())
                    .payload(payload)
                    .processed(false)
                    .build();

            outboxEventJpaRepository.save(outboxEvent);
        } catch (Exception e) {
            log.error("Failed to save outbox event for projectRequestId={}", event.projectRequestId(), e);
        }
    }

    /**
     * 프로젝트 생성 이벤트 처리
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleProjectCreatedEvent(ProjectCreatedEvent event) {
        try {
            String payload = objectMapper.writeValueAsString(event);

            OutboxEventEntity outboxEvent = OutboxEventEntity.builder()
                    .aggregateType(AggregateType.PROJECT)
                    .aggregateId(event.projectId())
                    .eventType(EventType.PROJECT_CREATED)
                    .payload(payload)
                    .processed(false)
                    .build();

            outboxEventJpaRepository.save(outboxEvent);
        } catch (Exception e) {
            log.error("Failed to save outbox event for projectId={}", event.projectId(), e);
        }
    }
}
