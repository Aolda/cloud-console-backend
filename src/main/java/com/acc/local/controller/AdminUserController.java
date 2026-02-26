package com.acc.local.controller;

import com.acc.global.common.PageRequest;
import com.acc.global.common.PageResponse;
import com.acc.global.security.session.SessionPrincipal;
import com.acc.local.controller.docs.AdminUserDocs;
import com.acc.local.dto.auth.*;
import com.acc.local.service.ports.AuthServicePort;
import com.acc.local.service.ports.UserServicePort;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class AdminUserController implements AdminUserDocs {

    private final UserServicePort userServicePort;

    @Override
    public ResponseEntity<AdminCreateUserResponse> createUser(
            @RequestBody @Validated AdminCreateUserRequest request,
            Authentication authentication
    ) {
        SessionPrincipal principal = (SessionPrincipal) authentication.getPrincipal();
        String sessionId = principal.getSessionId();

        AdminCreateUserResponse response = userServicePort.adminCreateUser(request, sessionId);

        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<AdminUpdateUserResponse> updateUser(
            @RequestBody @Validated AdminUpdateUserRequest request,
            Authentication authentication,
            @RequestParam String userId
    ) {
        SessionPrincipal principal = (SessionPrincipal) authentication.getPrincipal();
        String sessionId = principal.getSessionId();

        AdminUpdateUserResponse response = userServicePort.adminUpdateUser(request, sessionId, userId);


        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<Object> getUsers(
            @RequestParam(required = false) String userId,
            PageRequest page,
            Authentication authentication
    ) {
        SessionPrincipal principal = (SessionPrincipal) authentication.getPrincipal();
        String sessionId = principal.getSessionId();

        // userId가 있으면 상세 조회
        if (userId != null && !userId.isBlank()) {
            AdminGetUserResponse response = userServicePort.adminGetUser(userId, sessionId);
            return ResponseEntity.ok(response);
        }

        // userId가 없으면 목록 조회
        PageResponse<AdminListUsersResponse> response = userServicePort.adminListUsers(page, sessionId);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<Void> deleteUser(
            @RequestParam String userId,
            Authentication authentication
    ) {
        SessionPrincipal principal = (SessionPrincipal) authentication.getPrincipal();
        String sessionId = principal.getSessionId();

        userServicePort.adminDeleteUser(userId, sessionId);

        return ResponseEntity.noContent().build();
    }
}
