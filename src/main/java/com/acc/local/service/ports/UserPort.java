package com.acc.local.service.ports;

import com.acc.local.dto.user.UserCreateRequest;
import com.acc.local.dto.user.UserResponse;

public interface UserPort {
    UserResponse createUser(String bearerToken, UserCreateRequest request);
}