package com.acc.local.domain.model.auth;

import com.acc.local.entity.RefreshTokenEntity;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * RefreshToken 도메인 모델
 */
@Getter
@Builder
public class RefreshToken {

    private String userId;
    private String refreshToken;
    private LocalDateTime expiresAt;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static RefreshToken from(RefreshTokenEntity entity) {
        if (entity == null) {
            return null;
        }

        return RefreshToken.builder()
                .userId(entity.getUserId())
                .refreshToken(entity.getRefreshToken())
                .expiresAt(entity.getExpiresAt())
                .isActive(entity.getIsActive())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    public RefreshTokenEntity toEntity() {
        return RefreshTokenEntity.builder()
                .userId(this.userId)
                .refreshToken(this.refreshToken)
                .expiresAt(this.expiresAt)
                .build();
    }
}