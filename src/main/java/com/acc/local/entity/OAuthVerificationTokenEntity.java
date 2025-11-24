package com.acc.local.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * OAuth 인증 검증 토큰 엔티티
 * OAuth 로그인 후 회원가입 시 쿠키 변조를 방지하기 위한 일회성 검증 토큰
 * 같은 이메일로 여러 번 OAuth 로그인 가능 (중도 이탈 후 재시도 허용)
 */
@Entity
@Table(name = "oauth_verification_tokens")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OAuthVerificationTokenEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "email", length = 255, nullable = false)
    private String email;

    @Column(name = "verification_token", nullable = false, length = 1000)
    private String verificationToken;

    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    @Column(name = "used", nullable = false)
    private Boolean used;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Builder
    private OAuthVerificationTokenEntity(String email, String verificationToken, LocalDateTime expiresAt) {
        LocalDateTime now = LocalDateTime.now();

        this.email = email;
        this.verificationToken = verificationToken;
        this.expiresAt = expiresAt;
        this.used = false;
        this.createdAt = now;
        this.updatedAt = now;
    }

    /**
     * 토큰 만료 여부 확인
     */
    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiresAt);
    }

    /**
     * 토큰 유효성 확인 (사용되지 않음 + 만료되지 않음)
     */
    public boolean isValid() {
        return !used && !isExpired();
    }
}
