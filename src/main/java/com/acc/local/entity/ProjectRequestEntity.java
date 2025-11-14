package com.acc.local.entity;

import com.acc.local.domain.enums.auth.ProjectStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * 프로젝트 생성 요청 엔티티
 */
@Entity
@Table(name = "project_requests")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProjectRequestEntity {

    @Id
    @Column(name = "project_request_id", length = 64, nullable = false)
    private String projectRequestId;

    @Column(name = "request_user_id", length = 64, nullable = false)
    private String requestUserId;

    @Column(name = "project_name", length = 255, nullable = false)
    private String projectName;

    @Column(name = "project_type", length = 255, nullable = false)
    private String projectType;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 255, nullable = false)
    private ProjectStatus status;

    @Column(name = "project_description", columnDefinition = "TEXT")
    private String projectDescription;

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