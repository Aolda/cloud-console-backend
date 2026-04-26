package com.acc.local.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * 사용자 기본정보 엔티티
 */
@Entity
@Table(name = "user_detail")
@Getter
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserDbExtraEntity {

    @Id
    @Column(name = "user_id", length = 64, nullable = false)
    private String userId;

    @Column(name = "user_phone_number", length = 20, nullable = false)
    private String userPhoneNumber;

    @Column(name = "is_admin", nullable = false)
    private Boolean isAdmin;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "user_name", nullable = false)
    private String userName;

    @Builder.Default
    @Column(name = "is_deleted", nullable = false)
    private Boolean isDeleted = false;

    @Column(name = "keycloak_user_id", length = 64, unique = true)
    private String keycloakUserId;
    // PK(user_id)는 여전히 keystoneUserId. Keycloak sub claim을 별도 저장.

    @Column(name = "keystone_username", length = 128)
    private String keystoneUsername;
    // Keystone 로그인 name (email prefix: guswp320)

    @Column(name = "keystone_password", length = 512)
    private String keystonePassword;
    // AES-256 암호화된 Keystone password

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        if (this.isDeleted == null) {
            this.isDeleted = false;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}