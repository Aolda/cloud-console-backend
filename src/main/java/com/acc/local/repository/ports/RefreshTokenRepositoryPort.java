package com.acc.local.repository.ports;

import com.acc.local.entity.RefreshTokenEntity;

import java.time.LocalDateTime;
import java.util.Optional;

public interface RefreshTokenRepositoryPort {

    RefreshTokenEntity save(RefreshTokenEntity refreshToken);

    Optional<RefreshTokenEntity> findById(String userId);

    Optional<RefreshTokenEntity> findByRefreshTokenAndIsActiveTrue(String refreshToken);

    long deactivateByTokenAtomically(String token, LocalDateTime now);
}