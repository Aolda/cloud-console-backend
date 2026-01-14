package com.acc.local.controller;

import com.acc.global.common.PageRequest;
import com.acc.global.common.PageResponse;
import com.acc.global.security.jwt.JwtInfo;
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
        JwtInfo jwtInfo = (JwtInfo) authentication.getPrincipal();
        String userId = jwtInfo.getUserId();
        CreateNoticeResponse response = noticeServicePort.adminCreateNotice(request, userId);
        return ResponseEntity.status(201).body(response);
    }

    @Override
    public ResponseEntity<?> getNotices(Authentication authentication, String noticeId, PageRequest page, NoticeFilterRequest filter) {
        JwtInfo jwtInfo = (JwtInfo) authentication.getPrincipal();
        String requesterId = jwtInfo.getUserId();

        // noticeId가 있으면 상세 조회, 없으면 목록 조회
        if (noticeId != null && !noticeId.isBlank()) {
            GetNoticeResponse response = noticeServicePort.adminGetNotice(noticeId, requesterId);
            return ResponseEntity.ok(response);
        } else {
            PageResponse<ListNoticesResponse> response = noticeServicePort.adminListNotices(page, filter, requesterId);
            return ResponseEntity.ok(response);
        }
    }

}
