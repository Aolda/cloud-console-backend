package com.acc.local.service.ports;

import com.acc.local.domain.enums.project.ProjectRole;
import com.acc.local.dto.auth.*;
import com.acc.local.dto.project.UserPermissionResponse;

public interface AuthServicePort {
    String authenticateAndGenerateJwt(String keycloakToken);
    ProjectRole getProjectPermission(String ProjectId , String userId);
    UserPermissionResponse getUserPermission(String keystoneProjectId, String userId);
    boolean validateJwt(String jwtToken);
    void invalidateUserTokens(String userId);
    CreateUserResponse createUser(CreateUserRequest createUserRequest ,String userId);
    GetUserResponse getUserDetail(String targetUserId, String requesterId);
    UpdateUserResponse updateUser(String targetUserId, UpdateUserRequest updateUserRequest, String requesterId);
    void deleteUser(String targetUserId, String requesterId);

    String issueProjectScopeToken(String projectId , String userId);
    String authenticateKeystoneAndGenerateJwt(KeystonePasswordLoginRequest request);
    LoginTokens login(KeystonePasswordLoginRequest request);
    ProjectTokenResponse issueProjectAccessToken(String userId, String projectId);
    LoginResponse refreshToken(String refreshToken);
    SignupResponse signup(SignupRequest request, String verificationToken);

	LoginedUserProfileResponse getUserLoginedProfile(String userId);
}
