package com.acc.local.entity;

import com.acc.local.domain.enums.auth.AuthType;
import com.acc.local.entity.id.UserIdentityId;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * 사용자 인증정보 엔티티
 * 복합키 (user_id + auth_type)를 사용하여 한 사용자가 여러 인증 방식을 가질 수 있음
 */
@Entity
@Table(name = "user_auth_detail")
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserIdentityEntity {

    @EmbeddedId
    private UserIdentityId id;

    @Column(name = "department", length = 100, nullable = false)
    private String department;

    @Column(name = "student_id", length = 32, nullable = false)
    private String studentId;

    @Column(name = "user_email", nullable = false)
    private String userEmail;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // 편의 메서드: userId 조회
    public String getUserId() {
        return id != null ? id.getUserId() : null;
    }

    // 편의 메서드: authType 조회
    public Integer getAuthType() {
        return id != null ? id.getAuthType() : null;
    }

    // Enum 변환 헬퍼 메서드
    public AuthType getAuthTypeEnum() {
        return id != null ? AuthType.fromCode(id.getAuthType()) : null;
    }

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