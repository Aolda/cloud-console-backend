package com.acc.local.entity;

import com.acc.local.domain.enums.auth.TermType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 약관 엔티티
 */
@Entity
@Table(name = "terms")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TermEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "term_id")
    private Long termId;

    @Enumerated(EnumType.STRING)
    @Column(name = "term_type", length = 50, nullable = false)
    private TermType termType;

    @Column(name = "title", length = 255)
    private String title;

    @Column(name = "content", columnDefinition = "TEXT")
    private String content;

    @Column(name = "is_required")
    private Boolean isRequired;

    @Column(name = "version", length = 20)
    private String version;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // 양방향 관계 - UserTermAgreementEntity
    @OneToMany(mappedBy = "term", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserTermAgreementEntity> userAgreements = new ArrayList<>();

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