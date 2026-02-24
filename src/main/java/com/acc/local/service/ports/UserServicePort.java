package com.acc.local.service.ports;

import com.acc.global.common.PageRequest;
import com.acc.global.common.PageResponse;
import com.acc.local.dto.auth.*;

public interface UserServicePort {
    AdminCreateUserResponse adminCreateUser(AdminCreateUserRequest request, String sessionId);
    AdminUpdateUserResponse adminUpdateUser(AdminUpdateUserRequest request, String sessionId, String userId);
    AdminGetUserResponse adminGetUser(String userId, String sessionId);
    PageResponse<AdminListUsersResponse> adminListUsers(PageRequest page, String sessionId);
    void adminDeleteUser(String userId, String sessionId);
}
