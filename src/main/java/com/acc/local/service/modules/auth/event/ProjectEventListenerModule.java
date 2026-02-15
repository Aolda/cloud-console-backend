package com.acc.local.service.modules.auth.event;

import com.acc.local.entity.OutboxEventEntity;
import com.acc.local.repository.jpa.OutboxEventJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class ProjectEventListenerModule {

    private final OutboxEventJpaRepository outboxEventJpaRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleProjectRequestEvent(ProjectRequestEvent event) {

        // Outbox 테이블에 저장
        OutboxEventEntity outboxEvent = OutboxEventEntity.builder()
                .aggregateType("ProjectRequest")
                .aggregateId(event.getProjectRequestId())
                .eventType("ProjectRequestCreated")
                .processed(false)
                .build();

        outboxEventJpaRepository.save(outboxEvent);
    }
}
