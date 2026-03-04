package com.acc.local.entity;

import com.acc.local.domain.enums.outbox.AggregateType;
import com.acc.local.domain.enums.outbox.EventType;
import com.acc.local.domain.enums.outbox.NotificationStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "outbox_events")
@Getter @Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OutboxEventEntity {

    // 재시도 횟수 상한
    public static final int MAX_RETRY_COUNT = 5;

    @Id @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "event_id", length = 64, nullable = false)
    private String eventId;

    @Enumerated(EnumType.STRING)
    @Column(name = "aggregate_type", length = 100, nullable = false)
    private AggregateType aggregateType;

    @Column(name = "aggregate_id", length = 64, nullable = false)
    private String aggregateId;

    @Enumerated(EnumType.STRING)
    @Column(name = "event_type", length = 100, nullable = false)
    private EventType eventType;

    @Column(name = "payload", columnDefinition = "TEXT")
    private String payload;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "discord_status", nullable = false)
    @Builder.Default
    private NotificationStatus discordStatus = NotificationStatus.PENDING;

    @Enumerated(EnumType.STRING)
    @Column(name = "email_status", nullable = false)
    @Builder.Default
    private NotificationStatus emailStatus = NotificationStatus.PENDING;

    // 공통 재시도 횟수 (두 채널이 항상 같이 시도되므로 공유)
    @Column(name = "retry_count", nullable = false)
    @Builder.Default
    private Integer retryCount = 0;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    public void markDiscordSent() {
        this.discordStatus = NotificationStatus.SUCCESS;
    }

    public void markEmailSent() {
        this.emailStatus = NotificationStatus.SUCCESS;
    }

    /**
     * 전송 실패 시 공유 재시도 횟수 증가.
     * 상한 초과 시 PENDING 상태인 채널을 포기(FAILED) 처리.
     */
    public void incrementRetry() {
        this.retryCount++;
        if (this.retryCount >= MAX_RETRY_COUNT) {
            if (this.discordStatus == NotificationStatus.PENDING) {
                this.discordStatus = NotificationStatus.FAILED;
            }
            if (this.emailStatus == NotificationStatus.PENDING) {
                this.emailStatus = NotificationStatus.FAILED;
            }
        }
    }

    public boolean needsDiscordRetry() {
        return discordStatus == NotificationStatus.PENDING;
    }

    public boolean needsEmailRetry() {
        return emailStatus == NotificationStatus.PENDING;
    }

    public boolean isFullyProcessed() {
        boolean discordDone = discordStatus == NotificationStatus.SUCCESS || discordStatus == NotificationStatus.FAILED;
        boolean emailDone = emailStatus == NotificationStatus.SUCCESS || emailStatus == NotificationStatus.FAILED;
        return discordDone && emailDone;
    }

    public void markAsFailed() {
        this.discordStatus = NotificationStatus.FAILED;
        this.emailStatus = NotificationStatus.FAILED;
    }
}
