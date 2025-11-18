package com.acc.local.service.adapters.auth;

import com.acc.global.common.PageRequest;
import com.acc.global.common.PageResponse;
import com.acc.global.exception.auth.AuthErrorCode;
import com.acc.global.exception.auth.AuthServiceException;
import com.acc.local.dto.auth.CreateRoleRequest;
import com.acc.local.dto.auth.CreateRoleResponse;
import com.acc.local.dto.auth.ListRolesResponse;
import com.acc.local.entity.UserDetailEntity;
import com.acc.local.repository.ports.UserRepositoryPort;
import com.acc.local.service.modules.auth.AuthModule;
import com.acc.local.service.modules.auth.RoleModule;
import com.acc.local.service.modules.auth.UserModule;
import com.acc.local.service.ports.RoleServicePort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@Primary
@RequiredArgsConstructor
public class RoleServiceAdapter implements RoleServicePort {

    private final AuthModule authModule;
    private final RoleModule roleModule;
    private final UserRepositoryPort userRepositoryPort;
    private final UserModule userModule;

    @Override
    public CreateRoleResponse adminCreateRole(CreateRoleRequest request, String requesterId) {

        // 권한 체크
        userModule.isAdminUser(requesterId);

        String adminToken = authModule.issueSystemAdminToken("admin-create-role");
        try {
            return roleModule.adminCreateRole(request, adminToken);
        } finally {
            authModule.invalidateSystemAdminToken(adminToken);
        }
    }

    @Override
    public PageResponse<ListRolesResponse> adminListRoles(PageRequest page, String name, String requesterId) {
        // 권한 체크
        userModule.isAdminUser(requesterId);

        String adminToken = authModule.issueSystemAdminToken("admin-list-roles");
        try {
            return roleModule.adminListRoles(page, name, adminToken);
        } finally {
            authModule.invalidateSystemAdminToken(adminToken);
        }
    }
}
