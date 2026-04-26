package com.acc.local.service.adapters.auth;

import com.acc.global.common.PageRequest;
import com.acc.global.common.PageResponse;
import com.acc.local.dto.auth.*;
import com.acc.local.service.modules.auth.NoticeModule;
import com.acc.local.service.modules.auth.UserModule;
import com.acc.local.service.modules.session.SessionModule;
import com.acc.local.service.ports.NoticeServicePort;
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
    private final SessionModule sessionModule;

    @Override
    public CreateNoticeResponse adminCreateNotice(CreateNoticeRequest request, String sessionId) {
        String requesterId = sessionModule.getKeystoneUserId(sessionId);

        // 권한 체크
        userModule.isAdminUser(requesterId);

        return noticeModule.adminCreateNotice(request, requesterId);
    }

    @Override
    public GetNoticeResponse adminGetNotice(String noticeId, String sessionId) {
        String requesterId = sessionModule.getKeystoneUserId(sessionId);

        // 권한 체크
        userModule.isAdminUser(requesterId);

        return noticeModule.adminGetNotice(noticeId);
    }

    @Override
    public PageResponse<ListNoticesResponse> adminListNotices(PageRequest page, NoticeFilterRequest filter, String sessionId) {
        String requesterId = sessionModule.getKeystoneUserId(sessionId);

        // 권한 체크
        userModule.isAdminUser(requesterId);
        return noticeModule.adminListNotices(page, filter);
    }

}
