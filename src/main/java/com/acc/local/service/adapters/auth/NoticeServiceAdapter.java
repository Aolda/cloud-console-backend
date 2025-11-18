package com.acc.local.service.adapters.auth;

import com.acc.global.common.PageRequest;
import com.acc.global.common.PageResponse;
import com.acc.global.exception.auth.AuthErrorCode;
import com.acc.global.exception.auth.AuthServiceException;
import com.acc.local.dto.auth.CreateNoticeRequest;
import com.acc.local.dto.auth.CreateNoticeResponse;
import com.acc.local.dto.auth.ListNoticesResponse;
import com.acc.local.entity.UserDetailEntity;
import com.acc.local.service.modules.auth.NoticeModule;
import com.acc.local.service.modules.auth.UserModule;
import com.acc.local.service.ports.NoticeServicePort;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@Primary
@RequiredArgsConstructor
public class NoticeServiceAdapter implements NoticeServicePort {

    private final NoticeModule noticeModule;
    private final UserModule userModule;
    @Override
    public CreateNoticeResponse adminCreateNotice(CreateNoticeRequest request, String requesterId) {
        // 권한 체크
        userModule.isAdminUser(requesterId);

        return noticeModule.adminCreateNotice(request, requesterId);
    }

    @Override
    public PageResponse<ListNoticesResponse> adminListNotices(PageRequest page, String requesterId) {
        // 권한 체크
        userModule.isAdminUser(requesterId);
        return noticeModule.adminListNotices(page);
    }
}
