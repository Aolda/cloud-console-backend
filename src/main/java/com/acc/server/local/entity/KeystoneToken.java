package com.acc.server.local.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class KeystoneToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 512, nullable = false)
    private String token;

    //추후 User 정보 등으로 저장 가능
    @Column(nullable = false)
    private String requestedBy;

    @Column(nullable = false)
    private String scope;

    private LocalDateTime expiresAt;

    private LocalDateTime issuedAt;

    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
