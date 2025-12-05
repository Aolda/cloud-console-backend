package com.acc.local.service.ports;

import com.acc.global.common.PageRequest;
import com.acc.global.common.PageResponse;
import com.acc.local.dto.auth.*;

public interface UserServicePort {
    AdminCreateUserResponse adminCreateUser(AdminCreateUserRequest request, String requesterId);
    AdminUpdateUserResponse adminUpdateUser(AdminUpdateUserRequest request, String requesterId, String userId);
    AdminGetUserResponse adminGetUser(String userId, String requesterId);
    PageResponse<AdminListUsersResponse> adminListUsers(PageRequest page, String requesterId);
    void adminDeleteUser(String userId, String requesterId);
}
