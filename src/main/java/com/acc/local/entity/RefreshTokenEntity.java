package com.acc.local.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Refresh Token 엔티티
 * userId를 PK로 사용하여 한 사용자당 하나의 Refresh Token만 유지
 */
@Entity
@Table(name = "refresh_tokens")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RefreshTokenEntity {

    @Id
    @Column(name = "user_id", length = 64, nullable = false)
    private String userId;

    @Column(name = "refresh_token", nullable = false, length = 1000)
    private String refreshToken;

    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Builder
    private RefreshTokenEntity(String userId, String refreshToken, LocalDateTime expiresAt) {
        LocalDateTime now = LocalDateTime.now();

        this.userId = userId;
        this.refreshToken = refreshToken;
        this.expiresAt = expiresAt;
        this.isActive = true;
        this.createdAt = now;
        this.updatedAt = now;
    }

    /**
     * Refresh Token 비활성화
     */
    public void deactivate() {
        this.isActive = false;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Refresh Token 갱신
     */
    public void updateRefreshToken(String refreshToken, LocalDateTime expiresAt) {
        this.refreshToken = refreshToken;
        this.expiresAt = expiresAt;
        this.isActive = true;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Refresh Token 만료 여부 확인
     */
    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiresAt);
    }

    /**
     * Refresh Token 유효성 확인 (활성화 상태 + 만료되지 않음)
     */
    public boolean isValid() {
        return isActive && !isExpired();
    }
}