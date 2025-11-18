package com.acc.local.repository.adapters;

import com.acc.global.common.PageResponse;
import com.acc.local.dto.auth.ListNoticesResponse;
import com.acc.local.entity.NoticeEntity;
import com.acc.local.repository.jpa.NoticeJpaRepository;
import com.acc.local.repository.modules.NoticeQueryDSLModule;
import com.acc.local.repository.ports.NoticeRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Primary
@RequiredArgsConstructor
public class NoticeRepositoryAdapter implements NoticeRepositoryPort {

    private final NoticeJpaRepository noticeJpaRepository;
    private final NoticeQueryDSLModule noticeQueryDSLModule;

    @Override
    public NoticeEntity save(NoticeEntity noticeEntity) {
        return noticeJpaRepository.save(noticeEntity);
    }

    @Override
    public Optional<NoticeEntity> findById(String noticeId) {
        return noticeJpaRepository.findById(noticeId);
    }

    @Override
    public Page<NoticeEntity> findAll(Pageable pageable) {
        return noticeJpaRepository.findAll(pageable);
    }

    @Override
    public void deleteById(String noticeId) {
        noticeJpaRepository.deleteById(noticeId);
    }

    @Override
    public long count() {
        return noticeJpaRepository.count();
    }

    @Override
    public PageResponse<ListNoticesResponse> findAllNotices(String marker, String direction, int limit) {
        return noticeQueryDSLModule.findAllNotices(marker, direction, limit);
    }
}
