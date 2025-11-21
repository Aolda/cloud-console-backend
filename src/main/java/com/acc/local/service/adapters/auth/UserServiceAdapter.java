package com.acc.local.service.adapters.auth;

import com.acc.global.common.PageRequest;
import com.acc.global.common.PageResponse;
import com.acc.global.exception.auth.AuthErrorCode;
import com.acc.global.exception.auth.AuthServiceException;
import com.acc.global.util.UserUtil;
import com.acc.local.dto.auth.*;
import com.acc.local.entity.UserDetailEntity;
import com.acc.local.repository.ports.UserRepositoryPort;
import com.acc.local.service.modules.auth.AuthModule;
import com.acc.local.service.modules.auth.UserModule;
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

    @Override
    public AdminCreateUserResponse adminCreateUser(AdminCreateUserRequest request, String requesterId) {

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
    public AdminUpdateUserResponse adminUpdateUser(AdminUpdateUserRequest request, String requesterId, String userId) {

        // 권한 체크
        userModule.isAdminUser(requesterId);

        UserUtil.validateEmailFormat(request.email());
        UserUtil.validatePhoneNumber(request.phoneNumber());
        UserUtil.validateStudentId(request.studentId());

        String adminToken = authModule.issueSystemAdminToken("admin-update-user");
        try {
            String updateUserId = userModule.adminUpdateUser(request, adminToken , userId);
            return AdminUpdateUserResponse.from(updateUserId);
        } finally {
            authModule.invalidateSystemAdminToken(adminToken);
        }
    }

    @Override
    public AdminGetUserResponse adminGetUser(String userId, String requesterId) {

        userModule.isAdminUser(requesterId);

        String adminToken = authModule.issueSystemAdminToken("admin-get-user");
        try {
            return userModule.adminGetUser(userId, adminToken);
        } finally {
            authModule.invalidateSystemAdminToken(adminToken);
        }
    }

    @Override
    public PageResponse<AdminListUsersResponse> adminListUsers(PageRequest page, String requesterId) {

        // 권한 체크
        userModule.isAdminUser(requesterId);

        String adminToken = authModule.issueSystemAdminToken("admin-list-users");
        try {
            return userModule.adminListUsers(page, adminToken);
        } finally {
            authModule.invalidateSystemAdminToken(adminToken);
        }
    }

    @Override
    public void adminDeleteUser(String userId, String requesterId) {
        // 권한 체크
        userModule.isAdminUser(requesterId);

        String adminToken = authModule.issueSystemAdminToken("admin-delete-user");
        try {
            userModule.adminDeleteUser(userId, adminToken);
        } finally {
            authModule.invalidateSystemAdminToken(adminToken);
        }
    }
}

