package com.acc.local.repository.jpa;

import com.acc.local.entity.RefreshTokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface RefreshTokenJpaRepository extends JpaRepository<RefreshTokenEntity, String> {
    Optional<RefreshTokenEntity> findByRefreshTokenAndIsActiveTrue(String refreshToken);

    /**
     * Refresh Token 원자적 비활성화 (조회 + 업데이트)
     * @return 업데이트된 행 수 (0이면 이미 사용되었거나 만료됨)
     */
    @Modifying
    @Query("UPDATE RefreshTokenEntity r SET r.isActive = false, r.updatedAt = :now " +
           "WHERE r.refreshToken = :token AND r.isActive = true AND r.expiresAt > :now")
    int deactivateByTokenAtomically(@Param("token") String token, @Param("now") LocalDateTime now);
}