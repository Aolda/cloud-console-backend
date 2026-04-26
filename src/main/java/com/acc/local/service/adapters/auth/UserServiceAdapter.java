package com.acc.local.service.adapters.auth;

import com.acc.global.common.PageRequest;
import com.acc.global.common.PageResponse;
import com.acc.global.util.UserUtil;
import com.acc.local.domain.model.auth.User;
import com.acc.local.dto.auth.*;
import com.acc.local.repository.ports.UserRepositoryPort;
import com.acc.local.service.modules.auth.AuthModule;
import com.acc.local.service.modules.auth.UserModule;
import com.acc.local.service.modules.session.SessionModule;
import com.acc.local.service.ports.UserServicePort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@Primary
@RequiredArgsConstructor
public class UserServiceAdapter implements UserServicePort {

    private final AuthModule authModule;
    private final UserModule userModule;
    private final UserRepositoryPort userRepositoryPort;
    private final SessionModule sessionModule;

    @Override
    public AdminCreateUserResponse adminCreateUser(AdminCreateUserRequest request, String sessionId) {
        String requesterId = sessionModule.getKeystoneUserId(sessionId);

        // 권한 체크
        userModule.isAdminUser(requesterId);

        UserUtil.validateEmailFormat(request.email());
        UserUtil.validatePhoneNumber(request.phoneNumber());
        UserUtil.validateStudentId(request.studentId());

        String adminToken = authModule.issueSystemAdminToken("admin-create-user");
        try {
            String userId = userModule.adminCreateUser(request, adminToken);
            return AdminCreateUserResponse.from(userId);
        } finally {
            authModule.invalidateSystemAdminToken(adminToken);
        }
    }

    @Override
    public AdminUpdateUserResponse adminUpdateUser(AdminUpdateUserRequest request, String sessionId, String userId) {
        String requesterId = sessionModule.getKeystoneUserId(sessionId);

        // 권한 체크
        userModule.isAdminUser(requesterId);

        UserUtil.validateEmailFormat(request.email());
        UserUtil.validatePhoneNumber(request.phoneNumber());
        UserUtil.validateStudentId(request.studentId());

        String adminToken = authModule.issueSystemAdminToken("admin-update-user");
        try {
            String updateUserId = userModule.adminUpdateUser(request, adminToken, userId);
            return AdminUpdateUserResponse.from(updateUserId);
        } finally {
            authModule.invalidateSystemAdminToken(adminToken);
        }
    }

    @Override
    public AdminGetUserResponse adminGetUser(String userId, String sessionId) {
        String requesterId = sessionModule.getKeystoneUserId(sessionId);

        userModule.isAdminUser(requesterId);

        String adminToken = authModule.issueSystemAdminToken("admin-get-user");
        try {
            // Module에서 User 도메인 모델 조회
            User user = userModule.getUserById(userId, adminToken);

            // Adapter에서 User → DTO 변환
            return AdminGetUserResponse.builder()
                    .userId(user.getUserId())
                    .username(user.getUsername())
                    .email(user.getEmail())
                    .department(user.getDepartment())
                    .studentId(user.getStudentId())
                    .phoneNumber(user.getPhoneNumber())
                    .isEnabled(user.getIsEnabled())
                    .isAdmin(user.getIsAdmin())
                    .isDeleted(user.getIsDeleted())
                    .build();
        } finally {
            authModule.invalidateSystemAdminToken(adminToken);
        }
    }

    @Override
    public PageResponse<AdminListUsersResponse> adminListUsers(PageRequest page, String sessionId) {
        String requesterId = sessionModule.getKeystoneUserId(sessionId);

        // 권한 체크
        userModule.isAdminUser(requesterId);

        String adminToken = authModule.issueSystemAdminToken("admin-list-users");
        try {
            // Module에서 PageResponse<User> 받기
            PageResponse<User> userPage = userModule.adminListUsers(page, adminToken);

            // map() 메서드로 User → AdminListUsersResponse 변환
            return userPage.map(user -> AdminListUsersResponse.builder()
                    .userId(user.getUserId())
                    .username(user.getUsername())
                    .email(user.getEmail())
                    .department(user.getDepartment())
                    .phoneNumber(user.getPhoneNumber())
                    .isAdmin(user.getIsAdmin())
                    .enabled(user.getIsEnabled())
                    .defaultProjectName(null)  // TODO: 프로젝트 이름 조회 필요시 추가
                    .build());

        } finally {
            authModule.invalidateSystemAdminToken(adminToken);
        }
    }

    @Override
    public void adminDeleteUser(String userId, String sessionId) {
        String requesterId = sessionModule.getKeystoneUserId(sessionId);

        // 권한 체크
        userModule.isAdminUser(requesterId);

        // 1. 사용자의 모든 토큰 무효화 (Access Token + Keystone Token + Refresh Token)
        authModule.invalidateServiceTokensByUserId(userId);
        authModule.invalidateRefreshTokenByUserId(userId);

        // 2. 사용자 삭제
        String adminToken = authModule.issueSystemAdminToken("admin-delete-user");
        try {
            userModule.adminDeleteUser(userId, adminToken);
        } finally {
            authModule.invalidateSystemAdminToken(adminToken);
        }
    }
}
