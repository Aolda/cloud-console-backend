package com.acc.local.service.adapters.auth;
import com.acc.local.domain.enums.ProjectPermission;
import com.acc.local.domain.model.KeystoneProject;
import com.acc.local.domain.model.User;
import com.acc.local.dto.auth.*;
import com.acc.local.service.modules.auth.AuthModule;
import com.acc.local.service.ports.AuthServicePort;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

@Service
@Primary
@RequiredArgsConstructor
public class AuthServiceAdapter implements AuthServicePort {
    private final AuthModule authModule;

    @Override
    public String issueKeystoneToken() {
        return authModule.issueKeystoneToken();
    }

    // keycloak 로그인 이후 redirect URL 엔드포인트에서 사용될 메서드
    @Override
    public String authenticateAndGenerateJwt(String keycloakToken) {
        return authModule.authenticateAndGenerateJwt(keycloakToken);
    }

    @Override
    public boolean validateJwt(String jwtToken) {
        return authModule.validateJwtToken(jwtToken);
    }

    @Override
    public void invalidateUserTokens(String userId) {
        authModule.invalidateUserTokens(userId);
    }

    @Override
    public CreateUserResponse createUser(CreateUserRequest createUserRequest , String userId) {

        // TODO: userid 를 통해, 요청을 보낸 사람이 Root인지 권한 확인

        User user = User.builder()
                .name(createUserRequest.userName())
                .email(createUserRequest.userEmail())
                .enabled(true)
                .build();

        User createdUser = authModule.createUser(user, userId);
        return CreateUserResponse.from(createdUser);
    }

    @Override
    public GetUserResponse getUserDetail(String targetUserId, String requesterId) {
        // TODO: requesterId를 통해, 요청을 보낸 사람이 Root or 본인인지 권한 확인

        User user = authModule.getUserDetail(targetUserId, requesterId);
        return GetUserResponse.from(user);
    }

    @Override
    public UpdateUserResponse updateUser(String targetUserId, UpdateUserRequest updateUserRequest, String requesterId) {
        // TODO: requesterId를 통해, 요청을 보낸 사람이 Root or 본인인지 권한 확인

        User user = User.builder()
                .name(updateUserRequest.userName())
                .email(updateUserRequest.userEmail())
                .description(updateUserRequest.description())
                .defaultProjectId(updateUserRequest.defaultProjectId())
                .enabled(updateUserRequest.enabled() != null ? updateUserRequest.enabled() : true)
                .build();

        User updatedUser = authModule.updateUser(targetUserId, user, requesterId);
        return UpdateUserResponse.from(updatedUser);
    }

    @Override
    public void deleteUser(String targetUserId, String requesterId) {
        // TODO: requesterId를 통해, 요청을 보낸 사람이 Root or 본인인지 권한 확인
        
        authModule.deleteUser(targetUserId, requesterId);
    }

    @Override
    public ProjectPermission getProjectPermission(String projectId, String userid) {
        return authModule.getProjectPermission(projectId, userid);
    }

    @Override
    public UserPermissionResponse getUserPermission(String keystoneProjectId, String userId) {
        ProjectPermission permission = authModule.getProjectPermission(keystoneProjectId, userId);

        return UserPermissionResponse.builder()
                .projectPermission(permission.name().toLowerCase())
                .projectId(keystoneProjectId)
                .build();
    }

    @Override
    public CreateProjectResponse createProject(CreateProjectRequest createProjectRequest, String userId) {

        // TODO: userId를 통해, 요청을 보낸 사람이 Root인지 권한 확인

        KeystoneProject project = KeystoneProject.builder()
                .name(createProjectRequest.name())
                .description(createProjectRequest.description())
                .domainId(createProjectRequest.domainId())
                .enabled(createProjectRequest.enabled() != null ? createProjectRequest.enabled() : true)
                .isDomain(createProjectRequest.isDomain() != null ? createProjectRequest.isDomain() : false)
                .parentId(createProjectRequest.parentId())
                .tags(createProjectRequest.tags())
                .options(createProjectRequest.options())
                .build();

        KeystoneProject createdProject = authModule.createProject(project, userId);
        return CreateProjectResponse.from(createdProject);
    }

    @Override
    public GetProjectResponse getProjectDetail(String projectId, String requesterId) {
        // TODO: requesterId를 통해, 요청을 보낸 사람이 Root or 해당 프로젝트 권한이 있는지 확인

        KeystoneProject project = authModule.getProjectDetail(projectId, requesterId);
        return GetProjectResponse.from(project);
    }

    @Override
    public UpdateProjectResponse updateProject(String projectId, UpdateProjectRequest updateProjectRequest, String requesterId) {
        // TODO: requesterId를 통해, 요청을 보낸 사람이 Root or 해당 프로젝트 권한이 있는지 확인

        KeystoneProject project = KeystoneProject.builder()
                .name(updateProjectRequest.name())
                .description(updateProjectRequest.description())
                .domainId(updateProjectRequest.domainId())
                .enabled(updateProjectRequest.enabled())
                .isDomain(updateProjectRequest.isDomain())
                .tags(updateProjectRequest.tags())
                .options(updateProjectRequest.options())
                .build();

        KeystoneProject updatedProject = authModule.updateProject(projectId, project, requesterId);
        return UpdateProjectResponse.from(updatedProject);
    }

    @Override
    public void deleteProject(String projectId, String requesterId) {
        // TODO: requesterId를 통해, 요청을 보낸 사람이 Root or 해당 프로젝트 권한이 있는지 확인

        authModule.deleteProject(projectId, requesterId);
    }

    @Override
    public String issueProjectScopeToken(String projectId, String userId) {
        return authModule.issueProjectScopeToken(projectId,userId);
    }

    @Override
    public String authenticateKeystoneAndGenerateJwt(KeystonePasswordLoginRequest request) {
        // TODO: 에러발생 시 logout로직 추가
        return authModule.authenticateKeystoneAndGenerateJwt(request);
    }
}
