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
                    event.markAsFailed();
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
                event.markAsFailed();
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

        NotificationResult result = notificationPort.sendProjectNotification(new ProjectRequestCreatedNotification(
                projectEvent.projectRequestId(),
                projectEvent.projectName(),
                projectEvent.projectDescription(),
                projectEvent.requesterId(),
                projectEvent.email()
        ));

        applyNotificationResult(event, result);
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

        NotificationResult result = notificationPort.sendProjectNotification(new ProjectRequestApprovedNotification(
                decisionEvent.projectRequestId(),
                decisionEvent.projectName(),
                decisionEvent.projectDescription(),
                decisionEvent.requesterId(),
                decisionEvent.email(),
                decisionEvent.createdProjectId()
        ));

        applyNotificationResult(event, result);
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

        NotificationResult result = notificationPort.sendProjectNotification(new ProjectRequestRejectedNotification(
                decisionEvent.projectRequestId(),
                decisionEvent.projectName(),
                decisionEvent.projectDescription(),
                decisionEvent.requesterId(),
                decisionEvent.email(),
                decisionEvent.rejectReason()
        ));

        applyNotificationResult(event, result);
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
            event.markAsFailed();
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

        NotificationResult result = notificationPort.sendProjectNotification(new ProjectCreatedNotification(
                projectEvent.projectId(),
                projectEvent.projectName(),
                projectEvent.projectOwnerId(),
                projectEvent.email()
        ));

        applyNotificationResult(event, result);
        outboxMessageRepositoryPort.save(event);
    }

    /**
     * NotificationResult를 OutboxEventEntity에 반영
     * 성공한 채널은 sent = true로, 실패한 채널은 retryCount를 증가시킵니다.
     */
    private void applyNotificationResult(OutboxEventEntity event, NotificationResult result) {
        if (event.needsDiscordRetry()) {
            if (result.discordSuccess()) {
                event.markDiscordSent();
                log.info("Discord notification succeeded: eventId={}", event.getEventId());
            }
        }

        if (event.needsEmailRetry()) {
            if (result.emailSuccess()) {
                event.markEmailSent();
                log.info("Email notification succeeded: eventId={}", event.getEventId());
            }
        }

        // 어느 채널이든 하나라도 실패했으면 공유 retryCount 증가
        if (!result.discordSuccess() || !result.emailSuccess()) {
            event.incrementRetry();
            log.warn("Notification failed (discord={}, email={}): eventId={}, retryCount={}",
                    result.discordSuccess(), result.emailSuccess(),
                    event.getEventId(), event.getRetryCount());
        }

        if (event.isFullyProcessed()) {
            log.info("Outbox event fully processed: eventId={}", event.getEventId());
        }
    }
}
