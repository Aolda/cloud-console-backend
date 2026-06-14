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
    GetUserResponse getUserDetail(String targetUserId, String requesterId);

    String authenticateKeystoneAndGenerateJwt(KeystonePasswordLoginRequest request);
    LoginTokens login(KeystonePasswordLoginRequest request);
    LoginTokens refreshToken(String refreshToken);
    SignupResponse signup(SignupRequest request, String verificationToken);

	LoginedUserProfileResponse getUserLoginedProfile(String userId);

	void logout(String userId);
}
