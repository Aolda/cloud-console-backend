package com.acc.local.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * 공지사항 엔티티
 */
@Entity
@Table(name = "notice")
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class NoticeEntity {

    @Id
    @Column(name = "notice_id", length = 64, nullable = false)
    private String noticeId;

    @Column(name = "notice_user_id", length = 64, nullable = false)
    private String noticeUserId;

    @Column(name = "notice_title", length = 255, nullable = false)
    private String noticeTitle;

    @Column(name = "notice_description", columnDefinition = "TEXT")
    private String noticeDescription;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "starts_at", nullable = false)
    private LocalDateTime startsAt;

    @Column(name = "ends_at", nullable = false)
    private LocalDateTime endsAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
