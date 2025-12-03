package com.acc.local.repository.adapters;

import com.acc.local.entity.RefreshTokenEntity;
import com.acc.local.repository.jpa.RefreshTokenJpaRepository;
import com.acc.local.repository.modules.RefreshTokenQueryDSLModule;
import com.acc.local.repository.ports.RefreshTokenRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class RefreshTokenRepositoryAdapter implements RefreshTokenRepositoryPort {

    private final RefreshTokenJpaRepository refreshTokenJpaRepository;
    private final RefreshTokenQueryDSLModule refreshTokenQueryDSLModule;

    @Override
    public RefreshTokenEntity save(RefreshTokenEntity refreshToken) {
        return refreshTokenJpaRepository.save(refreshToken);
    }

    @Override
    public Optional<RefreshTokenEntity> findById(String userId) {
        return refreshTokenJpaRepository.findById(userId);
    }

    @Override
    public Optional<RefreshTokenEntity> findByRefreshTokenAndIsActiveTrue(String refreshToken) {
        return refreshTokenJpaRepository.findByRefreshTokenAndIsActiveTrue(refreshToken);
    }

    @Override
    public long deactivateByTokenAtomically(String token, LocalDateTime now) {
        return refreshTokenQueryDSLModule.deactivateByTokenAtomically(token, now);
    }
}