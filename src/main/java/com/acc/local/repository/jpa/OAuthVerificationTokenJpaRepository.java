package com.acc.local.repository.jpa;

import com.acc.local.entity.OAuthVerificationTokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OAuthVerificationTokenJpaRepository extends JpaRepository<OAuthVerificationTokenEntity, Long> {

    /**
     * 이메일로 사용되지 않은 유효한 토큰 조회 (가장 최신 것)
     */
    Optional<OAuthVerificationTokenEntity> findFirstByEmailAndUsedFalseOrderByCreatedAtDesc(String email);
}