package com.acc.local.repository.adapters;

import com.acc.local.entity.OutboxEventEntity;
import com.acc.local.repository.jpa.OutboxEventJpaRepository;
import com.acc.local.repository.ports.OutboxMessageRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class OutboxMessageRepositoryAdapter implements OutboxMessageRepositoryPort {

    private final OutboxEventJpaRepository outboxEventJpaRepository;


    @Override
    public List<OutboxEventEntity> findByProcessedFalse() {
        return outboxEventJpaRepository.findByProcessedFalseOrderByCreatedAtAsc();
    }

    @Override
    public void save(OutboxEventEntity event) {
        outboxEventJpaRepository.save(event);
    }
}
