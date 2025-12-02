package com.acc.local.repository.modules;

import com.acc.local.entity.QSnapshotTaskEntity;
import com.acc.local.entity.SnapshotTaskEntity;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class SnapshotTaskQueryDSLModule {

    private final JPAQueryFactory queryFactory;
    private final QSnapshotTaskEntity snapshotTask = QSnapshotTaskEntity.snapshotTaskEntity;

    public Page<SnapshotTaskEntity> findByPolicyIdAndStartedAtAfter(Long policyId, LocalDateTime since, Pageable pageable) {
        JPAQuery<SnapshotTaskEntity> contentQuery = queryFactory
                .selectFrom(snapshotTask)
                .where(
                        snapshotTask.policyId.eq(policyId),
                        snapshotTask.startedAt.goe(since)
                );

        List<SnapshotTaskEntity> contents = contentQuery
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> countQuery = queryFactory
                .select(snapshotTask.count())
                .from(snapshotTask)
                .where(
                        snapshotTask.policyId.eq(policyId),
                        snapshotTask.startedAt.goe(since)
                );

        return PageableExecutionUtils.getPage(contents, pageable, countQuery::fetchOne);
    }
}

