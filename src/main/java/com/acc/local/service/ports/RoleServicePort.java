package com.acc.local.service.ports;

import com.acc.global.common.PageRequest;
import com.acc.global.common.PageResponse;
import com.acc.local.dto.auth.CreateRoleRequest;
import com.acc.local.dto.auth.CreateRoleResponse;
import com.acc.local.dto.auth.ListRolesResponse;

public interface RoleServicePort {

    CreateRoleResponse adminCreateRole(CreateRoleRequest request, String requesterId);

    PageResponse<ListRolesResponse> adminListRoles(PageRequest page, String name, String requesterId);
}