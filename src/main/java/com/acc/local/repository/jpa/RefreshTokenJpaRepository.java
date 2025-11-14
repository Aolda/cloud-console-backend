package com.acc.local.repository.jpa;

import com.acc.local.entity.RefreshTokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RefreshTokenJpaRepository extends JpaRepository<RefreshTokenEntity, String> {
    Optional<RefreshTokenEntity> findByRefreshTokenAndIsActiveTrue(String refreshToken);
}