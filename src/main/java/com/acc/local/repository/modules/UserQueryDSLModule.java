package com.acc.local.repository.modules;

import com.acc.local.entity.QUserDbExtraEntity;
import com.acc.local.entity.QUserIdentityEntity;
import com.acc.local.repository.dto.UserDBDto;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * User 관련 QueryDSL 조회 모듈
 * UserIdentity와 UserDbExtra를 조인하여 조회
 */
@Repository
@RequiredArgsConstructor
public class UserQueryDSLModule {

    private final JPAQueryFactory queryFactory;
    private final QUserIdentityEntity userIdentity = QUserIdentityEntity.userIdentityEntity;
    private final QUserDbExtraEntity userDbExtra = QUserDbExtraEntity.userDbExtraEntity;

    /**
     * userId로 User 관련 정보를 조인하여 조회
     * UserIdentity와 UserDbExtra를 inner join으로 조회
     *
     * @param userId 조회할 사용자 ID
     * @return UserDBDto (UserIdentity + UserDbExtra)
     */
    public Optional<UserDBDto> findUserByUserId(String userId) {
        UserDBDto result = queryFactory
                .select(Projections.constructor(UserDBDto.class,
                        userIdentity,
                        userDbExtra))
                .from(userIdentity)
                .innerJoin(userDbExtra).on(userIdentity.userId.eq(userDbExtra.userId))
                .where(userIdentity.userId.eq(userId))
                .fetchOne();

        return Optional.ofNullable(result);
    }

    /**
     * 여러 userId로 User 관련 정보를 조인하여 bulk 조회
     * UserIdentity와 UserDbExtra를 inner join으로 조회
     * 삭제되지 않은 사용자만 반환
     *
     * @param userIds 조회할 사용자 ID 목록
     * @return UserDBDto 리스트 (UserIdentity + UserDbExtra)
     */
    public List<UserDBDto> findUsersByUserIds(List<String> userIds) {
        return queryFactory
                .select(Projections.constructor(UserDBDto.class,
                        userIdentity,
                        userDbExtra))
                .from(userIdentity)
                .innerJoin(userDbExtra).on(userIdentity.userId.eq(userDbExtra.userId))
                .where(
                        userIdentity.userId.in(userIds),
                        userDbExtra.isDeleted.eq(false)  // 삭제되지 않은 사용자만
                )
                .fetch();
    }
}