package com.acc.local.repository.modules;

import com.acc.local.entity.QUserDbExtraEntity;
import com.acc.local.entity.QUserIdentityEntity;
import com.acc.local.repository.dto.UserDBDto;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
     * 다중 인증 정보가 있는 경우 가장 최근 updatedAt 기준으로 조회
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
                .innerJoin(userDbExtra).on(userIdentity.id.userId.eq(userDbExtra.userId))
                .where(userIdentity.id.userId.eq(userId))
                .orderBy(userIdentity.createdAt.desc())
                .fetchFirst();

        return Optional.ofNullable(result);
    }

    /**
     * 여러 userId로 User 관련 정보를 조인하여 bulk 조회
     * UserIdentity와 UserDbExtra를 inner join으로 조회
     * 삭제되지 않은 사용자만 반환
     * 다중 인증 정보가 있는 경우 각 userId별 가장 최근 createdAt 기준으로 조회
     *
     * @param userIds 조회할 사용자 ID 목록
     * @return UserDBDto 리스트 (UserIdentity + UserDbExtra)
     */
    public List<UserDBDto> findUsersByUserIds(List<String> userIds) {
        List<UserDBDto> allResults = queryFactory
                .select(Projections.constructor(UserDBDto.class,
                        userIdentity,
                        userDbExtra))
                .from(userIdentity)
                .innerJoin(userDbExtra).on(userIdentity.id.userId.eq(userDbExtra.userId))
                .where(
                        userIdentity.id.userId.in(userIds),
                        userDbExtra.isDeleted.eq(false)  // 삭제되지 않은 사용자만
                )
                .fetch();

        // 각 userId별로 가장 최근 createdAt인 identity만 반환
        return allResults.stream()
                .collect(Collectors.groupingBy(UserDBDto::getUserId))
                .values().stream()
                .map(dtos -> dtos.stream()
                        .max(Comparator.comparing(dto -> dto.userIdentity().getCreatedAt()))
                        .orElseThrow())
                .toList();
    }
}