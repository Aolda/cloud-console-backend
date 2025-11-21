package com.acc.local.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_tokens")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserTokenEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private String userId;

    @Column(name = "jwt_token", nullable = false, length = 1000)
    private String jwtToken;

    @Column(name = "keystone_unscoped_token", length = 1000)
    private String keystoneUnscopedToken;

    @Column(name = "is_active", nullable = false)
    private boolean isActive;

    @Column(name = "keystone_expires_at", nullable = false)
    private LocalDateTime keystoneExpiresAt;

    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Builder
    private UserTokenEntity(String userId, String jwtToken, String keystoneUnscopedToken,
                            LocalDateTime keystoneExpiresAt , LocalDateTime expiresAt, LocalDateTime createdAt, LocalDateTime updatedAt, boolean isActive ) {
        LocalDateTime now = LocalDateTime.now();

        this.userId = userId;
        this.jwtToken = jwtToken;
        this.isActive = true;

        this.keystoneUnscopedToken = keystoneUnscopedToken;
        this.keystoneExpiresAt = keystoneExpiresAt;

        this.expiresAt = now.plusSeconds(2 * 60 * 60);
        this.createdAt = now;
        this.updatedAt = now;
    }

    public void deactivate() {
        this.isActive = false;
        this.updatedAt = LocalDateTime.now();
    }

    public void updateTokens(String jwtToken, String keystoneUnscopedToken, LocalDateTime expiresAt) {
        this.jwtToken = jwtToken;
        this.keystoneUnscopedToken = keystoneUnscopedToken;
        this.expiresAt = expiresAt;
        this.isActive = true;
        this.updatedAt = LocalDateTime.now();
    }

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiresAt);
    }

    public boolean isValid() {
        return isActive && !isExpired();
    }

    public boolean isMappingUnscopedTokenExpired() {
        return LocalDateTime.now().isAfter(keystoneExpiresAt);
    }

    public void setInvalid() {
        this.isActive = false;
        this.updatedAt = LocalDateTime.now();
    }
}
