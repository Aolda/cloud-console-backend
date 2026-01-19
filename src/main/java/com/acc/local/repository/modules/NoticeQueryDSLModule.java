package com.acc.local.repository.modules;

import com.acc.global.common.PageResponse;
import com.acc.local.dto.auth.ListNoticesResponse;
import com.acc.local.dto.auth.NoticeFilterRequest;
import com.acc.local.entity.QNoticeEntity;
import com.acc.local.entity.QUserDbExtraEntity;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class NoticeQueryDSLModule {

    private final JPAQueryFactory queryFactory;
    private final QNoticeEntity notice = QNoticeEntity.noticeEntity;
    private final QUserDbExtraEntity userDetail = QUserDbExtraEntity.userDbExtraEntity;

    public PageResponse<ListNoticesResponse> findAllNotices(String marker, String direction, int limit, NoticeFilterRequest filter) {
        boolean isPrev = "prev".equals(direction);
        boolean isFetchAll = (limit == 0);

        // 기본 쿼리 생성
        JPAQuery<ListNoticesResponse> query = buildBaseQuery();

        // 필터 적용
        applyFilter(query, filter);

        // 마커 조건 적용
        applyMarkerCondition(query, marker, isPrev);

        // 정렬 적용
        applyOrdering(query, isPrev);

        // 조회 및 결과 처리
        List<ListNoticesResponse> results = fetchResults(query, limit, isFetchAll, isPrev);
        List<ListNoticesResponse> contents = extractContents(results, limit, isFetchAll);
        boolean hasMore = !isFetchAll && results.size() > limit;

        // 페이지 메타데이터 생성
        return buildPageResponse(contents, marker, isPrev, hasMore, isFetchAll);
    }

    /**
     * 기본 쿼리 생성 (조인 포함)
     */
    private JPAQuery<ListNoticesResponse> buildBaseQuery() {
        return queryFactory
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
    }

    /**
     * 필터 조건 적용
     */
    private void applyFilter(JPAQuery<ListNoticesResponse> query, NoticeFilterRequest filter) {
        if (filter != null && filter.activeOnly() != null && filter.activeOnly()) {
            LocalDateTime now = LocalDateTime.now();
            query.where(notice.startsAt.loe(now).and(notice.endsAt.goe(now)));
        }
    }

    /**
     * 마커 조건 적용 (createdAt 기준)
     */
    private void applyMarkerCondition(JPAQuery<ListNoticesResponse> query, String marker, boolean isPrev) {
        if (marker == null) {
            return;
        }

        LocalDateTime markerCreatedAt = queryFactory
                .select(notice.createdAt)
                .from(notice)
                .where(notice.noticeId.eq(marker))
                .fetchOne();

        if (markerCreatedAt != null) {
            BooleanExpression markerExpression = isPrev
                    ? notice.createdAt.gt(markerCreatedAt)
                    : notice.createdAt.lt(markerCreatedAt);
            query.where(markerExpression);
        }
    }

    /**
     * 정렬 적용 (createdAt 기준)
     */
    private void applyOrdering(JPAQuery<ListNoticesResponse> query, boolean isPrev) {
        query.orderBy(isPrev ? notice.createdAt.asc() : notice.createdAt.desc());
    }

    /**
     * 쿼리 실행 및 결과 조회
     */
    private List<ListNoticesResponse> fetchResults(JPAQuery<ListNoticesResponse> query, int limit, boolean isFetchAll, boolean isPrev) {
        if (!isFetchAll) {
            query.limit(limit + 1);
        }

        List<ListNoticesResponse> results = query.fetch();

        if (isPrev) {
            Collections.reverse(results);
        }

        return results;
    }

    /**
     * 실제 컨텐츠 추출 (limit 적용)
     */
    private List<ListNoticesResponse> extractContents(List<ListNoticesResponse> results, int limit, boolean isFetchAll) {
        if (isFetchAll) {
            return results;
        }

        boolean hasMore = results.size() > limit;
        return hasMore ? results.subList(0, limit) : results;
    }

    /**
     * PageResponse 생성
     */
    private PageResponse<ListNoticesResponse> buildPageResponse(
            List<ListNoticesResponse> contents,
            String marker,
            boolean isPrev,
            boolean hasMore,
            boolean isFetchAll) {

        if (isFetchAll) {
            return PageResponse.<ListNoticesResponse>builder()
                    .contents(contents)
                    .first(true)
                    .last(true)
                    .size(contents.size())
                    .nextMarker(null)
                    .prevMarker(null)
                    .build();
        }

        if (contents.isEmpty()) {
            return PageResponse.<ListNoticesResponse>builder()
                    .contents(contents)
                    .first(marker == null)
                    .last(true)
                    .size(0)
                    .nextMarker(null)
                    .prevMarker(null)
                    .build();
        }

        String firstItemId = contents.get(0).noticeId();
        String lastItemId = contents.get(contents.size() - 1).noticeId();

        boolean isFirst;
        boolean isLast;
        String prevMarker;
        String nextMarker;

        if (isPrev) {
            isFirst = !hasMore;
            isLast = isLastPage(lastItemId);
            prevMarker = isFirst ? null : firstItemId;
            nextMarker = isLast ? null : lastItemId;
        } else {
            isFirst = (marker == null);
            isLast = !hasMore;
            prevMarker = isFirst ? null : firstItemId;
            nextMarker = isLast ? null : lastItemId;
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
        LocalDateTime lastCreatedAt = queryFactory
                .select(notice.createdAt)
                .from(notice)
                .where(notice.noticeId.eq(lastItemId))
                .fetchOne();

        if (lastCreatedAt == null) {
            return true;
        }

        Integer count = queryFactory
                .selectOne()
                .from(notice)
                .where(notice.createdAt.lt(lastCreatedAt))
                .fetchFirst();
        return count == null;
    }
}
