package com.acc.local.repository.ports;

import com.acc.local.entity.OutboxEventEntity;

import java.util.List;

public interface OutboxMessageRepositoryPort {

    List<OutboxEventEntity> findPendingEvents();

    void save(OutboxEventEntity event);
}
