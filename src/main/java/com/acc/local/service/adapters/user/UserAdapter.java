package com.acc.local.service.adapters.user;

import com.acc.local.dto.user.UserCreateRequest;
import com.acc.local.dto.user.UserResponse;
import com.acc.local.service.modules.user.UserModule;
import com.acc.local.service.ports.UserPort;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

@Service
@Primary
@RequiredArgsConstructor
public class UserAdapter implements UserPort {
    
    private final UserModule userModule;

    @Override
    public UserResponse createUser(String bearerToken, UserCreateRequest request) {
        return userModule.createUser(bearerToken, request);
    }
}