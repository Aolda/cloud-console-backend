package com.acc.local.repository.jpa;

import com.acc.local.entity.OutboxEventEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OutboxEventJpaRepository extends JpaRepository<OutboxEventEntity, String> {

    /**
     * 처리되지 않은 이벤트 조회
     */
    List<OutboxEventEntity> findByProcessedFalseOrderByCreatedAtAsc();

    /**
     * 특정 Aggregate ID에 대한 이벤트 조회
     */
    List<OutboxEventEntity> findByAggregateId(String aggregateId);
}
