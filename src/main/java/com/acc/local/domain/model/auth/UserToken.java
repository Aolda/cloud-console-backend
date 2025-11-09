package com.acc.local.domain.model.auth;

import com.acc.local.entity.UserTokenEntity;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * UserToken 도메인 모델
 */
@Getter
@Builder
public class UserToken {

    private Long id;
    private String userId;
    private String jwtToken;
    private String keystoneUnscopedToken;
    private boolean isActive;
    private LocalDateTime keystoneExpiresAt;
    private LocalDateTime expiresAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static UserToken from(UserTokenEntity entity) {
        if (entity == null) {
            return null;
        }

        return UserToken.builder()
                .id(entity.getId())
                .userId(entity.getUserId())
                .jwtToken(entity.getJwtToken())
                .keystoneUnscopedToken(entity.getKeystoneUnscopedToken())
                .isActive(entity.isActive())
                .keystoneExpiresAt(entity.getKeystoneExpiresAt())
                .expiresAt(entity.getExpiresAt())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    public UserTokenEntity toEntity() {
        return UserTokenEntity.builder()
                .userId(this.userId)
                .jwtToken(this.jwtToken)
                .keystoneUnscopedToken(this.keystoneUnscopedToken)
                .keystoneExpiresAt(this.keystoneExpiresAt)
                .build();
    }
}