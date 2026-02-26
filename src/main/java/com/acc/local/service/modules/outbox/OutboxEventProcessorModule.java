package com.acc.local.service.modules.outbox;

import com.acc.local.domain.enums.outbox.EventType;
import com.acc.local.dto.notification.*;
import com.acc.local.entity.OutboxEventEntity;
import com.acc.local.external.ports.NotificationExternalPort;
import com.acc.local.repository.ports.OutboxMessageRepositoryPort;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class OutboxEventProcessorModule {

    private final OutboxMessageRepositoryPort outboxMessageRepositoryPort;
    private final ObjectMapper objectMapper;
    private final NotificationExternalPort notificationPort;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void processEvent(OutboxEventEntity event) {
        try {
            switch (event.getAggregateType()) {
                case PROJECT_REQUEST -> processProjectRequestEvent(event);
                case PROJECT -> processProjectEvent(event);
                default -> {
                    log.warn("Unknown aggregate type: {}", event.getAggregateType());
                    event.markAsProcessed();
                    outboxMessageRepositoryPort.save(event);
                }
            }
        } catch (Exception e) {
            log.error("Error processing outbox event: eventId={}", event.getEventId(), e);
            throw new RuntimeException("Failed to process outbox event", e);
        }
    }

    /**
     * ProjectRequest 관련 이벤트 처리
     */
    private void processProjectRequestEvent(OutboxEventEntity event) throws Exception {
        switch (event.getEventType()) {
            case PROJECT_REQUEST_CREATED -> handleProjectRequestCreated(event);
            case PROJECT_REQUEST_APPROVED -> handleProjectRequestApproved(event);
            case PROJECT_REQUEST_REJECTED -> handleProjectRequestRejected(event);
            default -> {
                log.warn("Unknown project request event type: {}", event.getEventType());
                event.markAsProcessed();
                outboxMessageRepositoryPort.save(event);
            }
        }
    }

    /**
     * 프로젝트 요청 생성 이벤트 처리
     */
    private void handleProjectRequestCreated(OutboxEventEntity event) throws Exception {
        ProjectRequestEvent projectEvent = objectMapper.readValue(
                event.getPayload(),
                ProjectRequestEvent.class
        );

        notificationPort.sendProjectNotification(new ProjectRequestCreatedNotification(
                projectEvent.projectRequestId(),
                projectEvent.projectName(),
                projectEvent.projectDescription(),
                projectEvent.requesterId(),
                projectEvent.email()
        ));

        event.markAsProcessed();
        outboxMessageRepositoryPort.save(event);
    }

    /**
     * 프로젝트 요청 승인 이벤트 처리
     */
    private void handleProjectRequestApproved(OutboxEventEntity event) throws Exception {
        ProjectRequestDecisionEvent decisionEvent = objectMapper.readValue(
                event.getPayload(),
                ProjectRequestDecisionEvent.class
        );

        notificationPort.sendProjectNotification(new ProjectRequestApprovedNotification(
                decisionEvent.projectRequestId(),
                decisionEvent.projectName(),
                decisionEvent.projectDescription(),
                decisionEvent.requesterId(),
                decisionEvent.email(),
                decisionEvent.createdProjectId()
        ));

        event.markAsProcessed();
        outboxMessageRepositoryPort.save(event);
    }

    /**
     * 프로젝트 요청 거부 이벤트 처리
     */
    private void handleProjectRequestRejected(OutboxEventEntity event) throws Exception {
        ProjectRequestDecisionEvent decisionEvent = objectMapper.readValue(
                event.getPayload(),
                ProjectRequestDecisionEvent.class
        );

        notificationPort.sendProjectNotification(new ProjectRequestRejectedNotification(
                decisionEvent.projectRequestId(),
                decisionEvent.projectName(),
                decisionEvent.projectDescription(),
                decisionEvent.requesterId(),
                decisionEvent.email(),
                decisionEvent.rejectReason()
        ));

        event.markAsProcessed();
        outboxMessageRepositoryPort.save(event);
    }

    /**
     * Project 관련 이벤트 처리
     */
    private void processProjectEvent(OutboxEventEntity event) throws Exception {
        if (event.getEventType() == EventType.PROJECT_CREATED) {
            handleProjectCreated(event);
        } else {
            log.warn("Unknown project event type: {}", event.getEventType());
            event.markAsProcessed();
            outboxMessageRepositoryPort.save(event);
        }
    }

    /**
     * 프로젝트 생성 이벤트 처리
     */
    private void handleProjectCreated(OutboxEventEntity event) throws Exception {
        ProjectCreatedEvent projectEvent = objectMapper.readValue(
                event.getPayload(),
                ProjectCreatedEvent.class
        );

        notificationPort.sendProjectNotification(new ProjectCreatedNotification(
                projectEvent.projectId(),
                projectEvent.projectName(),
                projectEvent.projectOwnerId(),
                projectEvent.email()
        ));

        event.markAsProcessed();
        outboxMessageRepositoryPort.save(event);
    }
}

