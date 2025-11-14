package com.acc.local.repository.modules;

import com.acc.global.common.PageResponse;
import com.acc.local.dto.keypair.KeypairListResponse;
import com.acc.local.entity.QKeypairEntity;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class KeypairQueryDSLModule {

    private final JPAQueryFactory queryFactory;
    private final QKeypairEntity keypair = QKeypairEntity.keypairEntity;

    public PageResponse<KeypairListResponse> findKeypairsByProjectId(String projectId, String marker, String direction, int limit) {

        JPAQuery<KeypairListResponse> query = queryFactory
                .select(Projections.constructor(KeypairListResponse.class,
                        keypair.keypairId,
                        keypair.keypairName
                ))
                .from(keypair)
                .where(keypair.project.projectId.eq(projectId));  // projectId로 필터링

        // marker 기준으로 조회
        boolean isPrev = "prev".equals(direction);
        if (marker != null) {
            BooleanExpression markerExpression = isPrev ? keypair.keypairId.lt(marker)
                                                        : keypair.keypairId.gt(marker);
            query.where(markerExpression);
        }

        // 정렬
        query.orderBy(isPrev ? keypair.keypairId.desc() : keypair.keypairId.asc());

        // 조회 (limit > 0 일때, limit + 1 조회)
        boolean isFetchAll = (limit == 0);

        if (!isFetchAll) {
            query.limit(limit + 1);
        }
        List<KeypairListResponse> results = query.fetch();

        if (isPrev) {
            Collections.reverse(results);
        }

        boolean hasMore;
        List<KeypairListResponse> contents;

        if (isFetchAll) {
            contents = results;
            hasMore = false;
        } else {
            hasMore = results.size() > limit;
            contents = hasMore ? results.subList(0, limit) : results;
        }

        String nextMarker = null;
        String prevMarker = null;
        boolean isFirst = false;
        boolean isLast = false;

        if (isFetchAll) {  // 전체 조회
            isFirst = true;
            isLast = true;
        } else if (!contents.isEmpty()) {  // 페이지네이션
            String firstItemId = contents.get(0).getKeypairId();
            String lastItemId = contents.get(contents.size() - 1).getKeypairId();

            if (isPrev) {
                prevMarker = hasMore ? firstItemId : null;
                nextMarker = lastItemId;
                isFirst = !hasMore;
                isLast = isLastPage(projectId, lastItemId);
            } else {  // next
                prevMarker = (marker != null) ? firstItemId : null;
                nextMarker = hasMore ? lastItemId : null;
                isFirst = (marker == null);
                isLast = !hasMore;
            }
        } else {  // isFetchAll = false, contents가 비어있는 경우
            isFirst = (marker == null);
            isLast = true;
        }

        return PageResponse.<KeypairListResponse>builder()
                .contents(contents)
                .first(isFirst)
                .last(isLast)
                .size(contents.size())
                .nextMarker(nextMarker)
                .prevMarker(prevMarker)
                .build();
    }

    private boolean isLastPage(String projectId, String lastItemId) {
        Integer count = queryFactory
                .selectOne()
                .from(keypair)
                .where(
                        keypair.project.projectId.eq(projectId),
                        keypair.keypairId.gt(lastItemId)
                )
                .fetchFirst();
        return count == null;
    }
}
