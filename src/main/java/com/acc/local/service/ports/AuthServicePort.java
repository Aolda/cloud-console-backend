package com.acc.local.service.ports;

import com.acc.global.common.PageRequest;
import com.acc.global.common.PageResponse;
import com.acc.local.domain.enums.auth.ProjectPermission;
import com.acc.local.dto.auth.*;
import com.acc.local.dto.project.CreateProjectRequest;
import com.acc.local.dto.project.CreateProjectResponse;
import com.acc.local.dto.project.GetProjectResponse;
import com.acc.local.dto.project.UpdateProjectRequest;
import com.acc.local.dto.project.UpdateProjectResponse;
import com.acc.local.dto.project.UserPermissionResponse;

public interface AuthServicePort {
    String authenticateAndGenerateJwt(String keycloakToken);
    ProjectPermission getProjectPermission(String ProjectId , String userId);
    UserPermissionResponse getUserPermission(String keystoneProjectId, String userId);
    boolean validateJwt(String jwtToken);
    void invalidateUserTokens(String userId);
    CreateUserResponse createUser(CreateUserRequest createUserRequest ,String userId);
    GetUserResponse getUserDetail(String targetUserId, String requesterId);
    UpdateUserResponse updateUser(String targetUserId, UpdateUserRequest updateUserRequest, String requesterId);
    void deleteUser(String targetUserId, String requesterId);
    CreateProjectResponse createProject(CreateProjectRequest createProjectRequest, String userId);
    GetProjectResponse getProjectDetail(String projectId, String requesterId);
    UpdateProjectResponse updateProject(String projectId, UpdateProjectRequest updateProjectRequest, String requesterId);
    void deleteProject(String projectId, String requesterId);
    String issueProjectScopeToken(String projectId , String userId);
    String authenticateKeystoneAndGenerateJwt(KeystonePasswordLoginRequest request);
    LoginTokens login(KeystonePasswordLoginRequest request);
    ProjectTokenResponse issueProjectAccessToken(String userId, String projectId);
    LoginResponse refreshToken(String refreshToken);
    SignupResponse signup(SignupRequest request);
}
