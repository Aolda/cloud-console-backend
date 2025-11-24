package com.acc.local.domain.model.auth;

import com.acc.local.entity.OAuthVerificationTokenEntity;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * OAuth 인증 검증 토큰 도메인 모델
 */
@Getter
@Builder
public class OAuthVerificationToken {

    private String email;
    private String verificationToken;
    private LocalDateTime expiresAt;
    private Boolean used;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static OAuthVerificationToken from(OAuthVerificationTokenEntity entity) {
        if (entity == null) {
            return null;
        }

        return OAuthVerificationToken.builder()
                .email(entity.getEmail())
                .verificationToken(entity.getVerificationToken())
                .expiresAt(entity.getExpiresAt())
                .used(entity.getUsed())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    public OAuthVerificationTokenEntity toEntity() {
        return OAuthVerificationTokenEntity.builder()
                .email(this.email)
                .verificationToken(this.verificationToken)
                .expiresAt(this.expiresAt)
                .build();
    }
}