package com.acc.local.service.adapters.auth;

import com.acc.local.domain.enums.project.ProjectRole;
import com.acc.local.domain.model.auth.RefreshToken;
import com.acc.local.domain.model.auth.User;
import com.acc.local.dto.auth.UserKeystoneDto;
import com.acc.local.domain.model.auth.UserToken;
import com.acc.local.dto.auth.*;
import com.acc.local.dto.project.UserPermissionResponse;
import com.acc.local.service.modules.auth.AuthModule;
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
    private final UserModule userModule;

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
    public GetUserResponse getUserDetail(String targetUserId, String requesterId) {
        // TODO: requesterId를 통해, 요청을 보낸 사람이 Root or 본인인지 권한 확인

        UserKeystoneDto userKeystoneDto = authModule.getUserDetail(targetUserId, requesterId);
        return GetUserResponse.from(userKeystoneDto);
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
    public LoginTokens refreshToken(String refreshToken) {
        // 1. Refresh Token 검증 + 원자적 비활성화 + 새 토큰 발급 (동시 요청 방지를 위한 통합 로직)
        RefreshToken rotatedRefreshToken = authModule.validateAndRotateRefreshToken(refreshToken);
        String userId = rotatedRefreshToken.getUserId();

        // 2. Keystone + Access Token 재발급
        String newAccessToken = authModule.refreshKeystoneAndAccessToken(userId);

        return LoginTokens.from(newAccessToken, rotatedRefreshToken.getRefreshToken());
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
    public LoginedUserProfileResponse getUserLoginedProfile(String userId) {
        String adminToken = authModule.issueSystemAdminToken("ROOT_getUserLoginedProfile");

        try {
            // Module에서 User 도메인 모델 조회 (정합성 불일치 시 예외 발생)
            //TODO: 추후 정합성 맞추는 Flow 필요시 진행
            User user = userModule.getUserById(userId, adminToken);

            return LoginedUserProfileResponse.builder()
                .userName(user.getUsername())
                .univ(UnivDepartBriefDto.from(user))
                .build();
        } finally {
            authModule.invalidateSystemAdminToken(adminToken);
        }
    }

    @Override
    public void logout(String userId) {
        authModule.invalidateServiceTokensByUserId(userId);
        log.info("logout - 유저 토큰 삭제 성공 user: {}", userId);
    }
}
