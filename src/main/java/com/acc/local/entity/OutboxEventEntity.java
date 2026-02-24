package com.acc.local.entity;

import com.acc.local.domain.enums.outbox.AggregateType;
import com.acc.local.domain.enums.outbox.EventType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Outbox 패턴을 위한 이벤트 저장 엔티티
 */
@Entity
@Table(name = "outbox_events")
@Getter @Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OutboxEventEntity {

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

    @Column(name = "processed", nullable = false)
    @Builder.Default
    private Boolean processed = false;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "processed_at")
    private LocalDateTime processedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    public void markAsProcessed() {
        this.processed = true;
        this.processedAt = LocalDateTime.now();
    }
}
