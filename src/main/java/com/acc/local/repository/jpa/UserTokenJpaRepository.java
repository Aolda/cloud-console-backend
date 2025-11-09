package com.acc.local.repository.jpa;

import com.acc.local.entity.UserTokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserTokenJpaRepository extends JpaRepository<UserTokenEntity, Long> {

    // JWT 토큰으로 활성화된 UserToken Entity 조회
    Optional<UserTokenEntity> findByJwtTokenAndIsActiveTrue(String jwtToken);

    // 사용자별 활성화된 토큰 조회 (한 사용자당 하나의 활성 토큰만 유지)
    List<UserTokenEntity> findAllByUserIdAndIsActiveTrue(String userId);

    // 특정 사용자의 모든 활성 토큰을 비활성화 (새 로그인 시 기존 토큰 무효화)
    @Modifying
    @Query("UPDATE UserTokenEntity ut SET ut.isActive = false, ut.updatedAt = :now WHERE ut.userId = :userId AND ut.isActive = true")
    void deactivateAllByUserId(@Param("userId") String userId, @Param("now") LocalDateTime now);

    // 만료된 모든 토큰을 비활성화 (배치 작업용)
    @Modifying
    @Query("UPDATE UserTokenEntity ut SET ut.isActive = false, ut.updatedAt = :now WHERE ut.expiresAt < :now")
    void deactivateExpiredTokens(@Param("now") LocalDateTime now);

    // 비활성화된 토큰들을 물리적으로 삭제 (정리 작업용)
    void deleteByIsActiveFalse();
}
