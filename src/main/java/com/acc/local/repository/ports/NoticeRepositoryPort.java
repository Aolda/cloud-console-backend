package com.acc.local.repository.ports;

import com.acc.global.common.PageResponse;
import com.acc.local.dto.auth.ListNoticesResponse;
import com.acc.local.entity.NoticeEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface NoticeRepositoryPort {

    NoticeEntity save(NoticeEntity noticeEntity);

    Optional<NoticeEntity> findById(String noticeId);

    Page<NoticeEntity> findAll(Pageable pageable);

    void deleteById(String noticeId);

    long count();

    // 마커 기반 페이지네이션
    PageResponse<ListNoticesResponse> findAllNotices(String marker, String direction, int limit);
}
