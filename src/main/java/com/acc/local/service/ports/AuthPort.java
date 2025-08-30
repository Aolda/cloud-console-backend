package com.acc.local.service.ports;

import com.acc.local.domain.enums.ProjectPermission;
import com.acc.local.dto.auth.CreateUserRequest;
import com.acc.local.dto.auth.CreateUserResponse;
import com.acc.local.dto.auth.GetUserResponse;

public interface AuthPort {
    String issueKeystoneToken();
    String authenticateAndGenerateJwt(String keycloakToken);
    ProjectPermission getProjectPermission(String ProjectId , String userId);
    boolean validateJwt(String jwtToken);
    void invalidateUserTokens(String userId);
    CreateUserResponse createUser(CreateUserRequest createUserRequest ,String userId);
    GetUserResponse getUserDetail(String targetUserId, String requesterId);
}
