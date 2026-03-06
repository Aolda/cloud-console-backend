package com.acc.local.service.modules.outbox;

import com.acc.global.exception.outbox.OutboxErrorCode;
import com.acc.global.exception.outbox.OutboxEventException;
import com.acc.global.properties.OutboxProperties;
import com.acc.local.domain.enums.outbox.EventType;
import com.acc.local.dto.notification.*;
import com.acc.local.entity.OutboxEventEntity;
import com.acc.local.external.ports.NotificationExternalPort;
import com.acc.local.repository.ports.OutboxMessageRepositoryPort;
import com.fasterxml.jackson.core.JsonProcessingException;
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
    private final OutboxProperties outboxProperties;

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
        } catch (OutboxEventException e) {
            if (e.isUnrecoverable()) {
                // 역직렬화 실패, 알 수 없는 타입 등
                // 재시도해도 의미 없으므로 즉시 FAILED 처리
                log.error("Unrecoverable outbox event error, marking as FAILED: eventId={}, code={}",
                        event.getEventId(), e.getErrorCode().getCode(), e);
                event.markAsFailed();
                outboxMessageRepositoryPort.save(event);
            } else {
                // 알림 전송 실패 등 일시적 오류
                // retryCount는 applyNotificationResult()에서 증가
                log.error("Transient outbox event error, will retry: eventId={}, code={}",
                        event.getEventId(), e.getErrorCode().getCode(), e);
            }
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error processing outbox event: eventId={}", event.getEventId(), e);
            throw new OutboxEventException(OutboxErrorCode.EVENT_PROCESSING_FAILED,
                    "eventId=" + event.getEventId(), e);
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
            default -> throw new OutboxEventException(OutboxErrorCode.UNKNOWN_EVENT_TYPE,
                    "eventId=" + event.getEventId() + ", eventType=" + event.getEventType());
        }
    }

    /**
     * 프로젝트 요청 생성 이벤트 처리
     */
    private void handleProjectRequestCreated(OutboxEventEntity event) throws Exception {
        ProjectRequestEvent projectEvent = deserialize(event, ProjectRequestEvent.class);

        NotificationResult result = notificationPort.sendProjectNotification(
                new ProjectRequestCreatedNotification(
                        projectEvent.projectRequestId(),
                        projectEvent.projectName(),
                        projectEvent.projectDescription(),
                        projectEvent.requesterId(),
                        projectEvent.email()
                ),
                NotificationResult.ofPendingChannels(event.needsDiscordRetry(), event.needsEmailRetry())
        );

        applyNotificationResult(event, result);
        outboxMessageRepositoryPort.save(event);
    }

    /**
     * 프로젝트 요청 승인 이벤트 처리
     */
    private void handleProjectRequestApproved(OutboxEventEntity event) throws Exception {
        ProjectRequestDecisionEvent decisionEvent = deserialize(event, ProjectRequestDecisionEvent.class);

        NotificationResult result = notificationPort.sendProjectNotification(
                new ProjectRequestApprovedNotification(
                        decisionEvent.projectRequestId(),
                        decisionEvent.projectName(),
                        decisionEvent.projectDescription(),
                        decisionEvent.requesterId(),
                        decisionEvent.email(),
                        decisionEvent.createdProjectId()
                ),
                NotificationResult.ofPendingChannels(event.needsDiscordRetry(), event.needsEmailRetry())
        );

        applyNotificationResult(event, result);
        outboxMessageRepositoryPort.save(event);
    }

    /**
     * 프로젝트 요청 거부 이벤트 처리
     */
    private void handleProjectRequestRejected(OutboxEventEntity event) throws Exception {
        ProjectRequestDecisionEvent decisionEvent = deserialize(event, ProjectRequestDecisionEvent.class);

        NotificationResult result = notificationPort.sendProjectNotification(
                new ProjectRequestRejectedNotification(
                        decisionEvent.projectRequestId(),
                        decisionEvent.projectName(),
                        decisionEvent.projectDescription(),
                        decisionEvent.requesterId(),
                        decisionEvent.email(),
                        decisionEvent.rejectReason()
                ),
                NotificationResult.ofPendingChannels(event.needsDiscordRetry(), event.needsEmailRetry())
        );

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
            throw new OutboxEventException(OutboxErrorCode.UNKNOWN_EVENT_TYPE,
                    "eventId=" + event.getEventId() + ", eventType=" + event.getEventType());
        }
    }

    /**
     * 프로젝트 생성 이벤트 처리
     */
    private void handleProjectCreated(OutboxEventEntity event) throws Exception {
        ProjectCreatedEvent projectEvent = deserialize(event, ProjectCreatedEvent.class);

        NotificationResult result = notificationPort.sendProjectNotification(
                new ProjectCreatedNotification(
                        projectEvent.projectId(),
                        projectEvent.projectName(),
                        projectEvent.projectOwnerId(),
                        projectEvent.email()
                ),
                NotificationResult.ofPendingChannels(event.needsDiscordRetry(), event.needsEmailRetry())
        );

        applyNotificationResult(event, result);
        outboxMessageRepositoryPort.save(event);
    }

    private <T> T deserialize(OutboxEventEntity event, Class<T> clazz) {
        try {
            return objectMapper.readValue(event.getPayload(), clazz);
        } catch (JsonProcessingException e) {
            throw new OutboxEventException(OutboxErrorCode.PAYLOAD_DESERIALIZATION_FAILED,
                    "eventId=" + event.getEventId() + ", targetClass=" + clazz.getSimpleName(), e);
        }
    }

    /**
     * NotificationResult를 OutboxEventEntity에 반영합니다.
     * 각 채널(Discord/Email)의 결과에 따라 상태(discordStatus/emailStatus)를 SENT 등으로 갱신하고,
     * 하나라도 실패한 채널이 있으면 공유 retryCount를 증가시킵니다.
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
            event.incrementRetry(outboxProperties.getMaxRetryCount());
            log.warn("Notification failed (discord={}, email={}): eventId={}, retryCount={}",
                    result.discordSuccess(), result.emailSuccess(),
                    event.getEventId(), event.getRetryCount());
        }

        if (event.isFullyProcessed()) {
            log.info("Outbox event fully processed: eventId={}", event.getEventId());
        }
    }
}
