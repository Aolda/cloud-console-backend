package com.acc.local.service.ports;

import com.acc.local.domain.enums.ProjectPermission;
import com.acc.local.dto.auth.CreateProjectRequest;
import com.acc.local.dto.auth.CreateProjectResponse;
import com.acc.local.dto.auth.CreateUserRequest;
import com.acc.local.dto.auth.GetProjectResponse;
import com.acc.local.dto.auth.CreateUserResponse;
import com.acc.local.dto.auth.GetUserResponse;
import com.acc.local.dto.auth.UpdateProjectRequest;
import com.acc.local.dto.auth.UpdateProjectResponse;
import com.acc.local.dto.auth.UpdateUserRequest;
import com.acc.local.dto.auth.UpdateUserResponse;
import com.acc.local.dto.auth.KeystonePasswordLoginRequest;
import com.acc.local.dto.auth.UserPermissionResponse;
import com.acc.local.dto.auth.KeystonePasswordLoginRequest;

public interface AuthServicePort {
    String issueKeystoneToken();
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
}
