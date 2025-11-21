package com.acc.local.domain.model.auth;

import com.acc.local.dto.auth.KeystoneToken;
import com.acc.local.entity.UserTokenEntity;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * UserToken 도메인 모델
 */
@Getter
@Builder(toBuilder = true)
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
                .isActive(this.isActive)
                .keystoneExpiresAt(this.keystoneExpiresAt)
                .expiresAt(this.expiresAt)
                .createdAt(this.createdAt)
                .updatedAt(this.updatedAt)
                .build();
    }


    public static UserToken updateJwtWithProjectId(UserToken existingUserToken, String newJwtToken ,LocalDateTime expiresAt) {
        return UserToken.builder()
                .id(existingUserToken.getId())
                .userId(existingUserToken.getUserId())
                .jwtToken(newJwtToken)
                .keystoneUnscopedToken(existingUserToken.getKeystoneUnscopedToken())
                .keystoneExpiresAt(existingUserToken.getKeystoneExpiresAt())
                .isActive(existingUserToken.isActive())
                .expiresAt(expiresAt)
                .createdAt(existingUserToken.getCreatedAt())
                .build();
    }

    public static UserToken updateKeystoneByRefreshToken( UserToken existingUserToken,String newAccessToken, KeystoneToken newKeystoneToken, String userId, LocalDateTime expiresAt) {
         return existingUserToken.toBuilder()
                .userId(userId)
                .jwtToken(newAccessToken)
                .keystoneUnscopedToken(newKeystoneToken.token())
                .keystoneExpiresAt(newKeystoneToken.expiresAt())
                 .isActive(true)
                 .expiresAt(expiresAt)
                 .updatedAt(LocalDateTime.now())
                .build();
    }


}