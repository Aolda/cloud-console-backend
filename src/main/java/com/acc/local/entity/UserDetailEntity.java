package com.acc.local.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 사용자 기본정보 엔티티
 */
@Entity
@Table(name = "user_detail")
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserDetailEntity {

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

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private UserAuthDetailEntity userAuth;

    @Column(name = "user_name", nullable = false)
    private String userName;

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