package com.acc.local.repository.modules;

import com.acc.local.entity.QRefreshTokenEntity;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
@RequiredArgsConstructor
public class RefreshTokenQueryDSLModule {

    private final JPAQueryFactory queryFactory;
    private final QRefreshTokenEntity refreshToken = QRefreshTokenEntity.refreshTokenEntity;

    /**
     * Refresh Token 원자적 비활성화 (조회 + 업데이트)
     * @param token Refresh Token 값
     * @param now 현재 시간
     * @return 업데이트된 행 수 (0이면 이미 사용되었거나 만료됨)
     */
    public long deactivateByTokenAtomically(String token, LocalDateTime now) {
        return queryFactory
                .update(refreshToken)
                .set(refreshToken.isActive, false)
                .set(refreshToken.updatedAt, now)
                .where(
                        refreshToken.refreshToken.eq(token),
                        refreshToken.isActive.eq(true),
                        refreshToken.expiresAt.gt(now)
                )
                .execute(); // execute 실행 시, 즉시 쿼리 발행 (영속성 컨텍스트 우회 DB 접근)
    }
}