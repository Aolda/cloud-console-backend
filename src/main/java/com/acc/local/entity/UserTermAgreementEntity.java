package com.acc.local.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * 사용자-약관 동의 엔티티
 */
@Entity
@Table(name = "user_term_agreements")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserTermAgreementEntity {

    @EmbeddedId
    private UserTermAgreementId id;

    @MapsId("termId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "term_id", nullable = false)
    private TermEntity term;

    @MapsId("userId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserDetailEntity user;

    @Column(name = "is_agreed", nullable = false)
    private Boolean isAgreed;

    @Column(name = "agreed_at")
    private LocalDateTime agreedAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

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