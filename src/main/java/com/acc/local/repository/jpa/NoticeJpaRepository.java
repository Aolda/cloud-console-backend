package com.acc.local.repository.jpa;

import com.acc.local.entity.NoticeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NoticeJpaRepository extends JpaRepository<NoticeEntity, String> {
}