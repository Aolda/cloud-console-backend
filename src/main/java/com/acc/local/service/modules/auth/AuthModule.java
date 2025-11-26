package com.acc.local.service.modules.auth;

import com.acc.global.exception.AccBaseException;
import com.acc.global.exception.auth.AuthErrorCode;
import com.acc.global.exception.auth.AuthServiceException;
import com.acc.global.exception.auth.JwtAuthenticationException;
import com.acc.local.dto.auth.*;
import com.acc.local.repository.ports.UserRepositoryPort;
import com.acc.local.repository.ports.OAuthVerificationTokenRepositoryPort;
import com.acc.global.properties.OpenstackProperties;
import com.acc.local.entity.OAuthVerificationTokenEntity;
import com.acc.local.domain.model.auth.OAuthVerificationToken;
import com.acc.local.external.ports.KeystoneAPIExternalPort;
import com.fasterxml.jackson.databind.JsonNode;
import com.acc.global.security.jwt.JwtUtils;
import com.acc.local.domain.enums.project.ProjectRole;
import com.acc.local.domain.model.auth.KeystoneUser;
import com.acc.local.domain.model.auth.RefreshToken;
import com.acc.local.domain.model.auth.UserToken;
import com.acc.local.entity.RefreshTokenEntity;
import com.acc.local.entity.UserTokenEntity;
import com.acc.local.external.modules.keystone.KeystoneAPIUtils;
import com.acc.local.repository.ports.RefreshTokenRepositoryPort;
import com.acc.local.repository.ports.UserTokenRepositoryPort;
import com.acc.local.entity.UserDetailEntity;
import com.acc.local.domain.model.auth.UserDetail;
import com.acc.local.domain.model.auth.UserAuthDetail;

import org.springframework.http.ResponseEntity;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import lombok.extern.slf4j.Slf4j;

import java.security.InvalidParameterException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

//TODO: AuthModule 이 너무 많은 의존성을 가지게 될 것 -> 상의 후 분리가 필요함. (User, Project, Token ..etc)
@Slf4j
@Component
@RequiredArgsConstructor
public class AuthModule {
    private final WebClient keystoneWebClient;
    private final JwtUtils jwtUtils;

    private final OpenstackProperties openstackProperties;

    private final UserTokenRepositoryPort userTokenRepositoryPort;
    private final RefreshTokenRepositoryPort refreshTokenRepositoryPort;
    private final KeystoneAPIExternalPort keystoneAPIExternalPort;
    private final UserRepositoryPort userRepositoryPort;
    private final OAuthVerificationTokenRepositoryPort oAuthVerificationTokenRepositoryPort;

    public String getProjectIdFromToken(String token) {
        JsonNode tokenInfo = keystoneWebClient.get()
                .uri("/identity/v3/auth/tokens")
                .header("X-Auth-Token", token)
                .header("X-Subject-Token", token)
                .retrieve()
                .bodyToMono(JsonNode.class)
                .block();

        return tokenInfo.path("token").path("project").path("id").asText();
    }
    public List<String> getRolesFromToken(String token) {
        JsonNode tokenInfo = keystoneWebClient.get()
                .uri("/identity/v3/auth/tokens")
                .header("X-Auth-Token", token)
                .header("X-Subject-Token", token)
                .retrieve()
                .bodyToMono(JsonNode.class)
                .block();

        JsonNode rolesNode = tokenInfo.path("token").path("roles");

        List<String> roles = new java.util.ArrayList<>();
        if (rolesNode.isArray()) {
            for (JsonNode role : rolesNode) {
                roles.add(role.path("name").asText());
            }
        }
        return roles;
    }

    @Transactional
    public String authenticateAndGenerateJwt(String keycloakToken) {
        // String userId = jwtUtils.extractUserIdFromKeycloakToken(keycloakToken);

        ResponseEntity<JsonNode> loginResponse = keystoneAPIExternalPort.requestFederateLogin(keycloakToken);
        if (loginResponse == null) {
            throw new JwtAuthenticationException(AuthErrorCode.KEYSTONE_TOKEN_GENERATION_FAILED);
        }

        KeystoneToken tokenInfo = KeystoneAPIUtils.extractKeystoneToken(loginResponse);
        return issueACCToken(tokenInfo);
    }

    public ProjectRole getProjectPermission(String projectId, String userId) {
        String keystoneToken = getUnscopedTokenByUserId(userId);

        ResponseEntity<JsonNode> tokenInfoResponse = keystoneAPIExternalPort.getTokenInfo(keystoneToken);
        if (tokenInfoResponse == null) {
            throw new JwtAuthenticationException(AuthErrorCode.KEYSTONE_TOKEN_AUTHENTICATION_FAILED);
        }
        KeystoneToken tokenInfo = KeystoneAPIUtils.extractKeystoneToken(tokenInfoResponse);

        ResponseEntity<JsonNode> permissionResponse = keystoneAPIExternalPort.getAccountPermissionList(tokenInfo.userId(), keystoneToken);
        if (permissionResponse == null) {
            throw new JwtAuthenticationException(AuthErrorCode.KEYSTONE_TOKEN_AUTHENTICATION_FAILED);
        }
        Map<String, ProjectRole> permissionMap = KeystoneAPIUtils.createUserPermissionMap(permissionResponse);

        return permissionMap.getOrDefault(projectId, ProjectRole.NONE);
    }

    public boolean validateJwtToken(String jwtToken) {

        if (!jwtUtils.validateToken(jwtToken)) {
            return false;
        }

        return userTokenRepositoryPort.findByJwtTokenAndIsActiveTrue(jwtToken)
                .map(UserTokenEntity::isValid)
                .orElse(false);
    }

    // 토큰 비활성화
    @Transactional
    public void invalidateServiceTokensByUserId(String userId) {
        List<UserTokenEntity> savedActiveTokens = userTokenRepositoryPort.findAllByUserIdAndIsActiveTrue(
            userId);
        List<String> activeKeystoneUnscopedTokens = savedActiveTokens.stream()
            .map(UserTokenEntity::getKeystoneUnscopedToken).toList();

        userTokenRepositoryPort.deactivateAllByUserId(userId);
        for (String unscopedToken: activeKeystoneUnscopedTokens) {
            keystoneAPIExternalPort.revokeToken(unscopedToken);
        }
    }

    @Transactional
    public void invalidateSystemAdminToken(String systemAdminToken) {
        KeystoneToken keystoneTokenObject = keystoneAPIExternalPort.getTokenObject(systemAdminToken);

        if (!keystoneTokenObject.isAdmin()) {
            throw new InvalidParameterException("시스템 관리자 토큰만 본 메서드를 통해 해제할 수 있습니다");
        }

        keystoneAPIExternalPort.revokeToken(systemAdminToken);
    }

    private boolean checkTokenPrivilegeSystemAdmin(ResponseEntity<JsonNode> tokenInfoResponse) {
        KeystoneToken keystoneToken = KeystoneAPIUtils.extractKeystoneToken(tokenInfoResponse);
        return keystoneToken.isAdmin();
    }

    @Transactional
    public KeystoneUser createUser(KeystoneUser keystoneUser, String userId) {
        String keystoneToken = getUnscopedTokenByUserId(userId);

        // Keystone에 사용자 생성
        Map<String, Object> userRequest = KeystoneAPIUtils.createKeystoneUserRequest(keystoneUser);
        ResponseEntity<JsonNode> response = keystoneAPIExternalPort.createUser(keystoneToken, userRequest);
        if (response == null) {
            throw new AuthServiceException(AuthErrorCode.KEYSTONE_USER_CREATION_FAILED, "사용자 생성 응답이 null입니다.");
        }
        KeystoneUser createdKeystoneUser = KeystoneAPIUtils.parseKeystoneUserResponse(response);


        // ACC DB에 사용자 정보 저장
        // TODO: API 개발 시, 확인 필요 - UserDetailEntity와 UserAuthDetailEntity로 분리하여 저장
        // userRepositoryPort.saveUserDetail(...);
        // userRepositoryPort.saveUserAuth(...);

        return createdKeystoneUser;
    }

    @Transactional
    public KeystoneUser getUserDetail(String targetUserId, String requesterId) {
        String keystoneToken = issueSystemAdminToken(requesterId);

        // Keystone에서 사용자 정보 조회
        ResponseEntity<JsonNode> response = keystoneAPIExternalPort.getUserDetail(targetUserId, keystoneToken);
        if (response == null) {
            throw new AuthServiceException(AuthErrorCode.KEYSTONE_USER_CREATION_FAILED, "사용자 조회 응답이 null입니다.");
        }
        KeystoneUser keystoneUser = KeystoneAPIUtils.parseKeystoneUserResponse(response);

        // TODO: API 개발 시, 확인 필요 - UserDetailEntity와 UserAuthDetailEntity에서 조회
        // ACC DB에서 추가 정보 조회 및 병합
        // userRepositoryPort.findUserDetailById(targetUserId)
        // userRepositoryPort.findUserAuthById(targetUserId)

        return keystoneUser;
    }

    @Transactional
    public KeystoneUser updateUser(String targetUserId, KeystoneUser keystoneUser, String requesterId) {
        String keystoneToken = getUnscopedTokenByUserId(requesterId);

        // Keystone 사용자 업데이트
        Map<String, Object> userRequest = KeystoneAPIUtils.createKeystoneUpdateUserRequest(keystoneUser);
        ResponseEntity<JsonNode> response = keystoneAPIExternalPort.updateUser(targetUserId, keystoneToken, userRequest);
        if (response == null) {
            throw new AuthServiceException(AuthErrorCode.KEYSTONE_USER_CREATION_FAILED, "사용자 업데이트 응답이 null입니다.");
        }
        KeystoneUser updatedKeystoneUser = KeystoneAPIUtils.parseKeystoneUserResponse(response);

        // ACC DB에 사용자 정보 업데이트
        // TODO: API 개발 시, 확인 필요 - UserDetailEntity와 UserAuthDetailEntity로 분리하여 업데이트
        // userRepositoryPort.saveUserDetail(...);
        // userRepositoryPort.saveUserAuth(...);

        return updatedKeystoneUser;
    }

    @Transactional
    public void deleteUser(String targetUserId, String requesterId) {
        String keystoneToken = getUnscopedTokenByUserId(requesterId);

        // Keystone에서 사용자 삭제
        keystoneAPIExternalPort.deleteUser(targetUserId, keystoneToken);

        // TODO: API 개발 시, 확인 필요 - UserDetailEntity와 UserAuthDetailEntity 삭제
        // ACC DB에서도 사용자 삭제
        // userRepositoryPort.deleteUserDetailById(targetUserId);
        // userRepositoryPort.deleteUserAuthById(targetUserId);
    }

    @Transactional
    public boolean isUserExistsByEmail(String email) {
        // TODO: API 개발 시, 확인 필요 - Email은 Keystone에서 관리하므로 이 메서드 재검토 필요
        return false;
    }

    public String issueProjectScopeToken(String projectId, String userId) {
        String unscopedToken = getUnscopedTokenByUserId(userId);
        KeystoneToken scopedToken = keystoneAPIExternalPort.getScopedToken(projectId, unscopedToken);

        return scopedToken.token();
    }

    public String getUnscopedTokenByUserId(String userId) {
        UserTokenEntity userToken = getAvailUserTokenEntities(userId).getFirst();
        checkUnscopedTokenExpired(userToken);

        return userToken.getKeystoneUnscopedToken();
    }

    private List<UserTokenEntity> getAvailUserTokenEntities(String userId) {
        List<UserTokenEntity> userTokens = userTokenRepositoryPort.findAllByUserIdAndIsActiveTrue(userId);
        if (userTokens.isEmpty()) {
            throw new JwtAuthenticationException(AuthErrorCode.NOT_FOUND_ACC_TOKEN);
        }

        return userTokens;
    }

    private void checkUnscopedTokenExpired(UserTokenEntity userToken) {
        if (userToken.isMappingUnscopedTokenExpired()) {
            throw new AuthServiceException(AuthErrorCode.KEYSTONE_TOKEN_EXPIRED);
        }
    }

    /**
     * 시스템 관리자 권한의 토큰을 발행합니다. 사용 직후 `invalidateUserToken()`을 통해 즉시폐기 바랍니다.
     * @param userId 요청하는 사용자ID
     * @return 시스템 관리자 토큰
     */
    public String issueSystemAdminToken(String userId) {
        // TODO: 추후 응답구조 변경을 통한 폐기 프로세스 자동화 필요

        KeystonePasswordLoginRequest systemAdminLoginRequest = new KeystonePasswordLoginRequest(
            openstackProperties.getSaUsername(),
            openstackProperties.getSaPassword(),
            "Default"
        );

        KeystoneToken adminToken = keystoneAPIExternalPort.getAdminToken(systemAdminLoginRequest);

        return adminToken.token();
    }

    /**
     * 시스템 관리자 권한 계정을 'admin project scope'로 발행합니다. 사용 직후 `invalidateUserToken()`을 통해 즉시폐기 바랍니다.
     * @param userId 요청하는 사용자ID
     * @return 시스템 관리자 토큰
     */
    public String issueSystemAdminTokenWithAdminProjectScope(String userId) {
        // TODO: 추후 응답구조 변경을 통한 폐기 프로세스 자동화 필요

        KeystonePasswordLoginRequest systemAdminLoginRequest = new KeystonePasswordLoginRequest(
            openstackProperties.getSaUsername(),
            openstackProperties.getSaPassword(),
            "Default"
        );

        KeystoneToken adminToken = keystoneAPIExternalPort.getAdminTokenWithAdminProjectScope(systemAdminLoginRequest);

        return adminToken.token();
    }

    @Transactional
    public String authenticateKeystoneAndGenerateJwt(KeystonePasswordLoginRequest passwordLoginRequest) {
        KeystoneToken keystoneToken = keystoneAPIExternalPort.getUnscopedToken(passwordLoginRequest);
        return issueACCToken(keystoneToken);
    }

    /**
     * 로그인 처리 - UserToken 모델 반환
     */
    @Transactional
    public UserToken generateAccessToken(KeystonePasswordLoginRequest passwordLoginRequest) {
        // 1. Keystone으로부터 토큰 받기
        KeystoneToken keystoneToken = keystoneAPIExternalPort.getUnscopedToken(passwordLoginRequest);
        if (keystoneToken == null) {
            throw new JwtAuthenticationException(AuthErrorCode.KEYSTONE_TOKEN_GENERATION_FAILED);
        }

        String userId = keystoneToken.userId();
        invalidateServiceTokensByUserId(userId);

        // 2. UserToken 모델 생성 및 저장
        UserToken userToken = createUserToken(userId, keystoneToken);

        // 4. 두 모델 반환
        return userToken;
    }

    /**
     * 최초 로그인 시에는 리프레시 토큰 반환
     */
    @Transactional
    public RefreshToken generateRefreshToken(KeystonePasswordLoginRequest passwordLoginRequest , String userId) {
        return createRefreshToken(userId);
    }

    /**
     * 프로젝트 진입 시 projectId가 포함된 토큰 발급
     * 기존 UserTokenEntity의 jwtToken만 업데이트 (Keystone 호출 없음)
     */
    @Transactional
    public UserToken issueProjectScopedToken(String userId, String projectId) {

        UserTokenEntity existingTokenEntity = getAvailUserTokenEntities(userId).getFirst();

        // keystoneUnscopedToken이 유효한지 확인
        checkUnscopedTokenExpired(existingTokenEntity);

        UserToken existingUserToken = UserToken.from(existingTokenEntity);

        String newJwtToken = jwtUtils.generateToken(userId, projectId);

        // 5. Domain Model 업데이트 (새로운 UserToken 생성)
        UserToken updatedToken = UserToken.updateJwtWithProjectId(existingUserToken , newJwtToken , jwtUtils.calculateExpirationDateTime());

        UserTokenEntity savedEntity = userTokenRepositoryPort.save(updatedToken.toEntity());

        return UserToken.from(savedEntity);
    }

    private UserToken createUserToken(String userId, KeystoneToken keystoneToken) {
        String accessToken = jwtUtils.generateToken(userId);

        UserToken userToken = UserToken.builder()
            .userId(userId)
            .jwtToken(accessToken)
            .keystoneUnscopedToken(keystoneToken.token())
            .keystoneExpiresAt(keystoneToken.expiresAt())
            .build();

        UserTokenEntity savedEntity = userTokenRepositoryPort.save(userToken.toEntity());
        return UserToken.from(savedEntity);
    }

    private RefreshToken createRefreshToken(String userId) {
        String refreshTokenValue = jwtUtils.generateRefreshToken(userId);

        RefreshToken refreshToken = RefreshToken.builder()
            .userId(userId)
            .refreshToken(refreshTokenValue)
            .expiresAt(LocalDateTime.now().plusDays(7))
            .build();

        RefreshTokenEntity savedEntity = refreshTokenRepositoryPort.save(refreshToken.toEntity());
        return RefreshToken.from(savedEntity);
    }

    private String issueACCToken(KeystoneToken keystoneToken) {
        if (keystoneToken == null) {
            return null;
        }

        String userId = keystoneToken.userId();
        String keystoneTokenString = keystoneToken.token();
        invalidateServiceTokensByUserId(userId);

        String jwtToken = jwtUtils.generateToken(userId);
        UserTokenEntity tokenEntity = UserTokenEntity.builder()
            .userId(userId)
            .jwtToken(jwtToken)
            .keystoneUnscopedToken(keystoneTokenString)
            .keystoneExpiresAt(keystoneToken.expiresAt())
            .build();

        userTokenRepositoryPort.save(tokenEntity);
        return jwtToken;
    }

    /**
     * Refresh Token으로 새로운 Access Token 발급
     * 기존 Keystone Token도 재발급하여 완전히 새로운 세션 생성
     */
    @Transactional
    public String refreshAccessToken(String refreshTokenValue) {
        // 1. Refresh Token 검증
        if (!jwtUtils.validateRefreshToken(refreshTokenValue)) {
            throw new JwtAuthenticationException(AuthErrorCode.INVALID_TOKEN);
        }

        // 2. Refresh Token에서 userId 추출
        String userId = jwtUtils.extractUserIdFromRefreshToken(refreshTokenValue);

        // 3. DB에서 Refresh Token 확인
        RefreshTokenEntity refreshTokenEntity = refreshTokenRepositoryPort
            .findByRefreshTokenAndIsActiveTrue(refreshTokenValue)
            .orElseThrow(() -> new JwtAuthenticationException(AuthErrorCode.INVALID_TOKEN));

        // 4. Refresh Token 만료 확인
        if (refreshTokenEntity.isExpired()) {
            throw new JwtAuthenticationException(AuthErrorCode.TOKEN_EXPIRED);
        }

        // 5. 기존 UserToken 조회
        UserTokenEntity existingTokenEntity = getAvailUserTokenEntities(userId).getFirst();
        UserToken existingUserToken = UserToken.from(existingTokenEntity);
        // 6. 기존 Keystone Token으로 새로운 Keystone Unscoped Token 발급
        String oldKeystoneToken = existingTokenEntity.getKeystoneUnscopedToken();
        KeystoneToken newKeystoneToken = keystoneAPIExternalPort.getUnscopedTokenByToken(oldKeystoneToken);

        if (newKeystoneToken == null) {
            throw new JwtAuthenticationException(AuthErrorCode.KEYSTONE_TOKEN_GENERATION_FAILED);
        }

        // 7. 기존 Keystone Token revoke
        keystoneAPIExternalPort.revokeToken(oldKeystoneToken);

        // 9. 새로운 JWT Access Token 발급 (projectId 없이)
        String newAccessToken = jwtUtils.generateToken(userId);

        // 10. 새로운 UserToken 생성 및 저장
        UserToken newUserToken = UserToken.updateKeystoneByRefreshToken( existingUserToken, newAccessToken, newKeystoneToken, userId, jwtUtils.calculateExpirationDateTime());

        userTokenRepositoryPort.save(newUserToken.toEntity());

        return newAccessToken;
    }

    /**
     * 회원가입 처리
     * System Admin 권한으로 Keystone 사용자 생성 후 ACC DB에 저장
     */
    @Transactional
    public String signup(SignupRequest request , String adminToken) {
        try {
            // 1. Keystone 사용자 생성 요청 생성 (email을 name에 매핑!)
            KeystoneUser newKeystoneUser = KeystoneUser.from(request);

            Map<String, Object> userRequest = KeystoneAPIUtils.createKeystoneUserRequest(newKeystoneUser);

            ResponseEntity<JsonNode> response = keystoneAPIExternalPort.createUser(adminToken, userRequest);

            if (response == null) {
                throw new AuthServiceException(AuthErrorCode.KEYSTONE_USER_CREATION_FAILED, "사용자 생성 응답이 null입니다.");
            }

            // 2. Keystone 응답에서 userId 추출
            KeystoneUser createdKeystoneUser = KeystoneAPIUtils.parseKeystoneUserResponse(response);
            String userId = createdKeystoneUser.getId();

            // 3. UserDetail 도메인 모델 생성 및 저장
            UserDetail userDetail = UserDetail.createForSignup(userId,request);
            UserDetailEntity userDetailEntity = userRepositoryPort.saveUserDetail(userDetail.toEntity());

            // 4. UserAuthDetail 도메인 모델 생성 및 저장
            UserAuthDetail userAuthDetail = UserAuthDetail.createForSignup(userId, request);
            userRepositoryPort.saveUserAuth(userAuthDetail.toEntity(userDetailEntity));

            return userId;

        } catch (AccBaseException e) {
            // KeystoneException, AuthServiceException 등은 그대로 throw
            throw e;
        } catch (Exception e) {
            // 그 외 예상치 못한 예외만 SIGN_UP_ERROR로 래핑
            log.error("회원가입 중 예상치 못한 에러 발생 - Email: {}", request.email(), e);
            throw new AuthServiceException(AuthErrorCode.SIGN_UP_ERROR);
        }
    }

    /**
     * OAuth 인증 검증 토큰 생성 및 저장
     * @param email 사용자 이메일
     * @return 생성된 JWT 검증 토큰
     */
    @Transactional
    public String generateOAuthVerificationToken(String email) {
        String verificationToken = jwtUtils.generateOAuthVerificationToken(email);
        LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(15);

        OAuthVerificationToken oAuthVerificationToken = OAuthVerificationToken.builder()
                .email(email)
                .verificationToken(verificationToken)
                .expiresAt(expiresAt)
                .build();

        oAuthVerificationTokenRepositoryPort.save(oAuthVerificationToken.toEntity());
        return verificationToken;
    }

    /**
     * OAuth 검증 토큰 유효성 확인 및 사용 처리
     * @param email 사용자 이메일
     * @param verificationToken 검증 토큰
     * @throws AuthServiceException 토큰이 유효하지 않거나 이미 사용된 경우
     */
    @Transactional
    public void verifyAndUseOAuthToken(String email, String verificationToken) {
        // 1. JWT 토큰 자체 검증
        if (!jwtUtils.validateOAuthVerificationToken(verificationToken)) {
            throw new AuthServiceException(AuthErrorCode.INVALID_TOKEN, "유효하지 않은 OAuth 검증 토큰입니다.");
        }

        // 2. JWT에서 이메일 추출 및 요청 이메일과 비교
        String tokenEmail = jwtUtils.extractEmailFromOAuthToken(verificationToken);
        if (!email.equals(tokenEmail)) {
            throw new AuthServiceException(AuthErrorCode.INVALID_TOKEN, "토큰의 이메일과 요청 이메일이 일치하지 않습니다.");
        }

        // 3. DB에서 토큰 조회 (가장 최신 것)
        OAuthVerificationTokenEntity tokenEntity = oAuthVerificationTokenRepositoryPort
                .findFirstByEmailAndUsedFalseOrderByCreatedAtDesc(email)
                .orElseThrow(() -> new AuthServiceException(AuthErrorCode.INVALID_TOKEN, "사용 가능한 OAuth 검증 토큰을 찾을 수 없습니다."));

        // 4. 토큰 유효성 확인 (만료 여부)
        if (!tokenEntity.isValid()) {
            throw new AuthServiceException(AuthErrorCode.TOKEN_EXPIRED, "OAuth 검증 토큰이 만료되었습니다.");
        }

        // 5. 토큰 삭제 (일회성 토큰이므로 하드 딜리트)
        oAuthVerificationTokenRepositoryPort.delete(tokenEntity);
    }
}
