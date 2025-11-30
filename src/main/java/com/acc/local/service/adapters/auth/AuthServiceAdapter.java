package com.acc.local.service.adapters.auth;

import com.acc.global.exception.AccBaseException;
import com.acc.global.exception.auth.AuthErrorCode;
import com.acc.local.domain.enums.project.ProjectRole;
import com.acc.local.domain.model.auth.RefreshToken;
import com.acc.local.domain.model.auth.KeystoneUser;
import com.acc.local.domain.model.auth.UserDetail;
import com.acc.local.domain.model.auth.UserToken;
import com.acc.local.dto.auth.*;
import com.acc.local.dto.project.ProjectServiceDto;
import com.acc.local.dto.project.UserPermissionResponse;
import com.acc.local.entity.UserDetailEntity;
import com.acc.local.repository.ports.UserRepositoryPort;
import com.acc.local.service.modules.auth.AuthModule;
import com.acc.local.service.modules.auth.ProjectModule;
import com.acc.local.service.modules.auth.UserModule;
import com.acc.local.service.ports.AuthServicePort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@Primary
@RequiredArgsConstructor
public class AuthServiceAdapter implements AuthServicePort {

    private final AuthModule authModule;
    private final UserRepositoryPort userRepositoryPort;
    private final UserModule userModule;
    private final ProjectModule projectModule;

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
        authModule.invalidateServiceTokensByUserId(userId);
    }

    @Deprecated
    @Override
    public CreateUserResponse createUser(CreateUserRequest createUserRequest , String userId) {

        // TODO: userid 를 통해, 요청을 보낸 사람이 Root인지 권한 확인

        KeystoneUser keystoneUser = KeystoneUser.from(createUserRequest);
        KeystoneUser createdKeystoneUser = authModule.createUser(keystoneUser, userId);
        return CreateUserResponse.from(createdKeystoneUser);
    }
    @Deprecated
    @Override
    public GetUserResponse getUserDetail(String targetUserId, String requesterId) {
        // TODO: requesterId를 통해, 요청을 보낸 사람이 Root or 본인인지 권한 확인

        KeystoneUser keystoneUser = authModule.getUserDetail(targetUserId, requesterId);
        return GetUserResponse.from(keystoneUser);
    }
    @Deprecated
    @Override
    public UpdateUserResponse updateUser(String targetUserId, UpdateUserRequest updateUserRequest, String requesterId) {
        // TODO: requesterId를 통해, 요청을 보낸 사람이 Root or 본인인지 권한 확인

        KeystoneUser keystoneUser = KeystoneUser.from(updateUserRequest);
        KeystoneUser updatedKeystoneUser = authModule.updateUser(targetUserId, keystoneUser, requesterId);
        return UpdateUserResponse.from(updatedKeystoneUser);
    }
    @Deprecated
    @Override
    public void deleteUser(String targetUserId, String requesterId) {
        // TODO: requesterId를 통해, 요청을 보낸 사람이 Root or 본인인지 권한 확인
        
        authModule.deleteUser(targetUserId, requesterId);
    }

    @Override
    public ProjectRole getProjectPermission(String projectId, String userid) {
        return authModule.getProjectPermission(projectId, userid);
    }

    @Override
    public UserPermissionResponse getUserPermission(String keystoneProjectId, String userId) {
        ProjectRole permission = authModule.getProjectPermission(keystoneProjectId, userId);

        return UserPermissionResponse.builder()
                .projectPermission(permission.name().toLowerCase())
                .projectId(keystoneProjectId)
                .build();
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

    @Override
    public LoginTokens login(KeystonePasswordLoginRequest request) {

        UserToken userToken = authModule.generateAccessToken(request);
        log.info(userToken.toString());

        RefreshToken refreshToken = authModule.generateRefreshToken(request, userToken.getUserId());
        log.info(refreshToken.toString());

        // 3. DTO로 변환하여 반환
        return LoginTokens.from(
            userToken.getJwtToken(),
            refreshToken.getRefreshToken()
        );
    }

    @Override
    public ProjectTokenResponse issueProjectAccessToken(String userId, String projectId) {
        // Module에서 UserToken 받기
        UserToken userToken = authModule.issueProjectScopedToken(userId, projectId);

        // DTO로 변환하여 반환
        return ProjectTokenResponse.builder()
                .accessToken(userToken.getJwtToken())
                .build();
    }

    @Override
    public LoginTokens refreshToken(String refreshToken) {
        // 1. Refresh Token 검증 및 userId 추출
        String userId = authModule.validateRefreshTokenAndGetUserId(refreshToken);

        // 2. Keystone + Access Token 재발급
        String newAccessToken = authModule.refreshKeystoneAndAccessToken(userId);

        // 3. Refresh Token Rotation (기존 토큰 말소 + 새 토큰 발급)
        String newRefreshToken = authModule.rotateRefreshToken(userId);

        return LoginTokens.from(newAccessToken, newRefreshToken);
    }

    @Override
    public SignupResponse signup(SignupRequest request, String verificationToken) {
        // 1. OAuth 검증 토큰 확인 및 사용 처리
        authModule.verifyAndUseOAuthToken(request.email(), verificationToken);
        log.info("OAuth 검증 토큰 검증 완료 - Email: {}", request.email());

        // 2. 회원가입 진행
        String adminToken = authModule.issueSystemAdminToken("signup-process");
        try {
            String userId = authModule.signup(request, adminToken);
            return SignupResponse.from(userId);
        } finally {
            // 3. 어드민 토큰 revoke (성공/실패 여부와 관계없이 항상 실행)
            authModule.invalidateSystemAdminToken(adminToken);
        }
    }

    @Override
    public LoginedUserProfileResponse getUserLoginedProfile(String userId, String projectId) {
        try {
            String adminToken = authModule.issueSystemAdminToken("ROOT_getUserLoginedProfile");
            AdminGetUserResponse adminGetUserResponse = userModule.adminGetUserWithoutAuthInfoResponse(userId, adminToken);
            authModule.invalidateSystemAdminToken(adminToken);

            // projectId가 존재하면 프로젝트 정보 조회
            ProjectServiceDto projectServiceDto = null;
            if (projectId != null && !projectId.isBlank()) {
                projectServiceDto = projectModule.getProjectDetail(projectId, userId);
            }

            if (adminGetUserResponse != null) {
                return LoginedUserProfileResponse.builder()
                    .userName(adminGetUserResponse.username())
                    .univ(UnivDepartBriefDto.from(adminGetUserResponse))
                    .project(projectServiceDto)
                    .build();
            }

            UserDetailEntity userDetailEntity = userModule.adminGetUserDetailDB(userId);
            return LoginedUserProfileResponse.builder()
                .userName(userDetailEntity.getUserName())
                .project(projectServiceDto)
                .build();

        } catch(Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    @Override
    public void logout(String userId) {
        authModule.invalidateServiceTokensByUserId(userId);
        log.info("logout - 유저 토큰 삭제 성공 user: {}", userId);
    }
}
