package com.acc.local.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
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

    @Column(name = "keystone_token", length = 1000)
    private String keystoneToken;

    @Column(name = "is_active", nullable = false)
    private boolean isActive;

    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    private UserTokenEntity(String userId, String jwtToken, String keystoneToken, 
                           LocalDateTime expiresAt) {
        this.userId = userId;
        this.jwtToken = jwtToken;
        this.keystoneToken = keystoneToken;
        this.isActive = true;
        this.expiresAt = expiresAt;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public static UserTokenEntity create(String userId, String jwtToken, String keystoneToken,
                                        LocalDateTime expiresAt) {
        return new UserTokenEntity(userId, jwtToken, keystoneToken, expiresAt);
    }

    public void deactivate() {
        this.isActive = false;
        this.updatedAt = LocalDateTime.now();
    }

    public void updateTokens(String jwtToken, String keystoneToken, LocalDateTime expiresAt) {
        this.jwtToken = jwtToken;
        this.keystoneToken = keystoneToken;
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
}