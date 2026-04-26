package com.acc.local.controller;

import com.acc.global.common.PageRequest;
import com.acc.global.common.PageResponse;
import com.acc.global.security.session.SessionPrincipal;
import com.acc.local.controller.docs.NoticeDocs;
import com.acc.local.dto.auth.*;
import com.acc.local.service.ports.NoticeServicePort;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class NoticeController implements NoticeDocs {

    private final NoticeServicePort noticeServicePort;

    @Override
    public ResponseEntity<CreateNoticeResponse> createNotice(CreateNoticeRequest request, Authentication authentication) {
        SessionPrincipal principal = (SessionPrincipal) authentication.getPrincipal();
        String sessionId = principal.getSessionId();
        CreateNoticeResponse response = noticeServicePort.adminCreateNotice(request, sessionId);
        return ResponseEntity.status(201).body(response);
    }

    @Override
    public ResponseEntity<?> getNotices(Authentication authentication, String noticeId, PageRequest page, NoticeFilterRequest filter) {
        SessionPrincipal principal = (SessionPrincipal) authentication.getPrincipal();
        String sessionId = principal.getSessionId();

        // noticeId가 있으면 상세 조회, 없으면 목록 조회
        if (noticeId != null && !noticeId.isBlank()) {
            GetNoticeResponse response = noticeServicePort.adminGetNotice(noticeId, sessionId);
            return ResponseEntity.ok(response);
        } else {
            PageResponse<ListNoticesResponse> response = noticeServicePort.adminListNotices(page, filter, sessionId);
            return ResponseEntity.ok(response);
        }
    }

}
