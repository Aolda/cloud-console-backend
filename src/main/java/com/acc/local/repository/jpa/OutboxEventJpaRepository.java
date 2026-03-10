package com.acc.local.repository.jpa;

import com.acc.local.domain.enums.outbox.NotificationStatus;
import com.acc.local.entity.OutboxEventEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OutboxEventJpaRepository extends JpaRepository<OutboxEventEntity, String> {

    List<OutboxEventEntity> findByDiscordStatusOrEmailStatusOrderByCreatedAtAsc(
            NotificationStatus discordStatus,
            NotificationStatus emailStatus
    );
}
