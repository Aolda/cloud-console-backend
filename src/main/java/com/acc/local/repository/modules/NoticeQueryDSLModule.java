package com.acc.local.repository.modules;

import com.acc.global.common.PageResponse;
import com.acc.local.dto.auth.ListNoticesResponse;
import com.acc.local.entity.QNoticeEntity;
import com.acc.local.entity.QUserDetailEntity;
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
public class NoticeQueryDSLModule {

    private final JPAQueryFactory queryFactory;
    private final QNoticeEntity notice = QNoticeEntity.noticeEntity;
    private final QUserDetailEntity userDetail = QUserDetailEntity.userDetailEntity;

    public PageResponse<ListNoticesResponse> findAllNotices(String marker, String direction, int limit) {

        JPAQuery<ListNoticesResponse> query = queryFactory
                .select(Projections.constructor(ListNoticesResponse.class,
                        notice.noticeId,
                        notice.noticeTitle,
                        notice.noticeDescription,
                        userDetail.userName,
                        notice.createdAt,
                        notice.startsAt,
                        notice.endsAt
                ))
                .from(notice)
                .leftJoin(userDetail).on(notice.noticeUserId.eq(userDetail.userId));

        // marker 기준으로 조회
        boolean isPrev = "prev".equals(direction);
        if (marker != null) {
            BooleanExpression markerExpression = isPrev ? notice.noticeId.lt(marker)
                                                        : notice.noticeId.gt(marker);
            query.where(markerExpression);
        }

        // 정렬 (noticeId 기준)
        query.orderBy(isPrev ? notice.noticeId.desc() : notice.noticeId.asc());

        // 조회 (limit > 0 일때, limit + 1 조회)
        boolean isFetchAll = (limit == 0);

        if (!isFetchAll) {
            query.limit(limit + 1);
        }
        List<ListNoticesResponse> results = query.fetch();

        if (isPrev) {
            Collections.reverse(results);
        }

        boolean hasMore;
        List<ListNoticesResponse> contents;

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
            String firstItemId = contents.get(0).noticeId();
            String lastItemId = contents.get(contents.size() - 1).noticeId();

            if (isPrev) {
                isFirst = !hasMore;
                isLast = isLastPage(lastItemId);
                prevMarker = isFirst ? null : firstItemId;
                nextMarker = isLast ? null : lastItemId;
            } else {  // next
                isFirst = (marker == null);
                isLast = !hasMore;
                prevMarker = isFirst ? null : firstItemId;
                nextMarker = isLast ? null : lastItemId;
            }
        } else {  // isFetchAll = false, contents가 비어있는 경우
            isFirst = (marker == null);
            isLast = true;
        }

        return PageResponse.<ListNoticesResponse>builder()
                .contents(contents)
                .first(isFirst)
                .last(isLast)
                .size(contents.size())
                .nextMarker(nextMarker)
                .prevMarker(prevMarker)
                .build();
    }

    private boolean isLastPage(String lastItemId) {
        Integer count = queryFactory
                .selectOne()
                .from(notice)
                .where(notice.noticeId.gt(lastItemId))
                .fetchFirst();
        return count == null;
    }
}
