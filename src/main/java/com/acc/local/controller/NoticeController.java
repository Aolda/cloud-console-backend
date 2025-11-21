package com.acc.local.controller;

import com.acc.global.common.PageRequest;
import com.acc.global.common.PageResponse;
import com.acc.global.security.jwt.JwtInfo;
import com.acc.local.controller.docs.NoticeDocs;
import com.acc.local.dto.auth.CreateNoticeRequest;
import com.acc.local.dto.auth.CreateNoticeResponse;
import com.acc.local.dto.auth.ListNoticesResponse;
import com.acc.local.dto.auth.ListRolesResponse;
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
        return ResponseEntity.ok(noticeServicePort.adminCreateNotice(request,userId));
    }

    @Override
    public ResponseEntity<PageResponse<ListNoticesResponse>> listNotice(PageRequest page, Authentication authentication) {
        JwtInfo jwtInfo = (JwtInfo) authentication.getPrincipal();
        String userId = jwtInfo.getUserId();
        return ResponseEntity.ok(noticeServicePort.adminListNotices(page,userId));
    }
}

