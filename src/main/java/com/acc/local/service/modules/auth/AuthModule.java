package com.acc.local.service.modules.auth;
import com.acc.global.exception.auth.AuthErrorCode;
import com.acc.global.exception.auth.JwtAuthenticationException;
import com.acc.global.exception.auth.KeystoneManagementException;
import com.acc.global.properties.OpenStackProperties;
import com.fasterxml.jackson.databind.JsonNode;
import com.acc.global.security.JwtUtils;
import com.acc.local.domain.enums.ProjectPermission;
import com.acc.local.domain.model.KeystoneProject;
import com.acc.local.domain.model.User;
import com.acc.local.entity.UserTokenEntity;
import com.acc.local.external.modules.keystone.*;
import com.acc.local.repository.ports.UserTokenRepositoryPort;
import com.acc.local.dto.auth.KeystoneTokenInfo;
import com.acc.local.dto.auth.KeystonePasswordLoginRequest;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.http.ResponseEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

//TODO: AuthModule 이 너무 많은 의존성을 가지게 될 것 -> 상의 후 분리가 필요함. (User, Project, Token ..etc)
@Component
@RequiredArgsConstructor
public class AuthModule {
    private final WebClient keystoneWebClient;
    private final OpenStackProperties openStackProperties;
    private final JwtUtils jwtUtils;
    private final UserTokenRepositoryPort userTokenRepositoryPort;

    private final KeystoneAuthAPIModule keystoneAuthAPIModule;
    private final KeystoneProjectAPIModule keystoneProjectAPIModule;
    private final KeystoneUserAPIModule keystoneUserAPIModule;
    private final KeystoneRoleAPIModule keystoneRoleAPIModule;

    @Deprecated // TODO: AuthController 측 의존성문제 해결 시 삭제예정
    public String issueKeystoneToken() {
        String username = openStackProperties.getKeystone().getUsername();
        String password = openStackProperties.getKeystone().getPassword();
        String projectName = openStackProperties.getKeystone().getProject();

        Map<String, Object> request = Map.of(
                "auth", Map.of(
                        "identity", Map.of(
                                "methods", List.of("password"),
                                "password", Map.of("user", Map.of(
                                        "name", username,
                                        "domain", Map.of("name", "default"),
                                        "password", password
                                ))
                        ),
                        "scope", Map.of("project", Map.of(
                                "name", projectName,
                                "domain", Map.of("name", "default")
                        ))
                )
        );

        return keystoneWebClient.post()
                .uri("/v3/auth/tokens")
                .bodyValue(request)
                .exchangeToMono(resp -> {
                    String token = resp.headers().asHttpHeaders().getFirst("X-Subject-Token");
                    return Mono.justOrEmpty(token);
                })
                .block();
    }
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

        String userId = jwtUtils.extractUserIdFromKeycloakToken(keycloakToken);

        userTokenRepositoryPort.deactivateAllByUserId(userId);

        ResponseEntity<JsonNode> loginResponse = keystoneAuthAPIModule.requestFederateLogin(keycloakToken);
        if (loginResponse == null) {
            throw new JwtAuthenticationException(AuthErrorCode.FAILED_TOKEN_AUTHENTICATION);
        }
        String keystoneToken = KeystoneAPIUtils.extractKeystoneToken(loginResponse);

        String jwtToken = jwtUtils.generateToken(userId, keystoneToken);

        LocalDateTime expiresAt = LocalDateTime.now().plusSeconds(86400); // 24시간 기본값

        UserTokenEntity tokenEntity = UserTokenEntity.create(userId, jwtToken, keystoneToken, expiresAt);
        userTokenRepositoryPort.save(tokenEntity);

        return jwtToken;

    }

    public ProjectPermission getProjectPermission(String projectId, String userId) {
        UserTokenEntity userToken = userTokenRepositoryPort.findByUserIdAndIsActiveTrue(userId)
                .orElseThrow(() -> new JwtAuthenticationException(AuthErrorCode.NOT_FOUND_ACC_TOKEN));
        String keystoneToken = userToken.getKeystoneUnscopedToken();
        
        ResponseEntity<JsonNode> tokenInfoResponse = keystoneAuthAPIModule.getTokenInfo(keystoneToken);
        if (tokenInfoResponse == null) {
            throw new JwtAuthenticationException(AuthErrorCode.FAILED_TOKEN_AUTHENTICATION);
        }
        KeystoneTokenInfo tokenInfo = KeystoneAPIUtils.extractKeystoneTokenInfo(tokenInfoResponse);
        
        ResponseEntity<JsonNode> permissionResponse = keystoneRoleAPIModule.getAccountPermissionList(tokenInfo.userId(), keystoneToken);
        if (permissionResponse == null) {
            throw new JwtAuthenticationException(AuthErrorCode.FAILED_TOKEN_AUTHENTICATION);
        }
        Map<String, ProjectPermission> permissionMap = KeystoneAPIUtils.createUserPermissionMap(permissionResponse);
        
        return permissionMap.getOrDefault(projectId, ProjectPermission.NONE);
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
    public void invalidateUserTokens(String userId) {
        userTokenRepositoryPort.deactivateAllByUserId(userId);
    }

    @Transactional
    public User createUser(User user, String userId) {
        UserTokenEntity userToken = userTokenRepositoryPort.findByUserIdAndIsActiveTrue(userId)
                .orElseThrow(() -> new JwtAuthenticationException(AuthErrorCode.NOT_FOUND_ACC_TOKEN));
        String keystoneToken = userToken.getKeystoneUnscopedToken();
        
        Map<String, Object> userRequest = KeystoneAPIUtils.createKeystoneUserRequest(user);
        ResponseEntity<JsonNode> response = keystoneUserAPIModule.createUser(keystoneToken, userRequest);
        if (response == null) {
            throw new KeystoneManagementException(AuthErrorCode.KEYSTONE_USER_CREATION_FAILED, "사용자 생성 응답이 null입니다.");
        }
        return KeystoneAPIUtils.parseKeystoneUserResponse(response);
    }

    @Transactional
    public User getUserDetail(String targetUserId, String requesterId) {
        UserTokenEntity userToken = userTokenRepositoryPort.findByUserIdAndIsActiveTrue(requesterId)
                .orElseThrow(() -> new JwtAuthenticationException(AuthErrorCode.NOT_FOUND_ACC_TOKEN));
        String keystoneToken = userToken.getKeystoneUnscopedToken();
        
        ResponseEntity<JsonNode> response = keystoneUserAPIModule.getUserDetail(targetUserId, keystoneToken);
        if (response == null) {
            throw new KeystoneManagementException(AuthErrorCode.KEYSTONE_USER_CREATION_FAILED, "사용자 조회 응답이 null입니다.");
        }
        return KeystoneAPIUtils.parseKeystoneUserResponse(response);
    }

    @Transactional
    public User updateUser(String targetUserId, User user, String requesterId) {
        UserTokenEntity userToken = userTokenRepositoryPort.findByUserIdAndIsActiveTrue(requesterId)
                .orElseThrow(() -> new JwtAuthenticationException(AuthErrorCode.NOT_FOUND_ACC_TOKEN));
        String keystoneToken = userToken.getKeystoneUnscopedToken();
        
        Map<String, Object> userRequest = KeystoneAPIUtils.createKeystoneUpdateUserRequest(user);
        ResponseEntity<JsonNode> response = keystoneUserAPIModule.updateUser(targetUserId, keystoneToken, userRequest);
        if (response == null) {
            throw new KeystoneManagementException(AuthErrorCode.KEYSTONE_USER_CREATION_FAILED, "사용자 업데이트 응답이 null입니다.");
        }
        return KeystoneAPIUtils.parseKeystoneUserResponse(response);
    }

    @Transactional
    public void deleteUser(String targetUserId, String requesterId) {
        UserTokenEntity userToken = userTokenRepositoryPort.findByUserIdAndIsActiveTrue(requesterId)
                .orElseThrow(() -> new JwtAuthenticationException(AuthErrorCode.NOT_FOUND_ACC_TOKEN));
        String keystoneToken = userToken.getKeystoneUnscopedToken();

        keystoneUserAPIModule.deleteUser(targetUserId, keystoneToken);
    }

    @Transactional
    public KeystoneProject createProject(KeystoneProject project, String userId) {
        UserTokenEntity userToken = userTokenRepositoryPort.findByUserIdAndIsActiveTrue(userId)
                .orElseThrow(() -> new JwtAuthenticationException(AuthErrorCode.NOT_FOUND_ACC_TOKEN));
        String keystoneToken = userToken.getKeystoneUnscopedToken();
        
        Map<String, Object> projectRequest = KeystoneAPIUtils.createKeystoneProjectRequest(project);
        ResponseEntity<JsonNode> response = keystoneProjectAPIModule.createProject(keystoneToken, projectRequest);
        if (response == null) {
            throw new KeystoneManagementException(AuthErrorCode.KEYSTONE_PROJECT_CREATION_FAILED, "프로젝트 생성 응답이 null입니다.");
        }
        return KeystoneAPIUtils.parseKeystoneProjectResponse(response);
    }

    @Transactional
    public KeystoneProject getProjectDetail(String projectId, String requesterId) {
        UserTokenEntity userToken = userTokenRepositoryPort.findByUserIdAndIsActiveTrue(requesterId)
                .orElseThrow(() -> new JwtAuthenticationException(AuthErrorCode.NOT_FOUND_ACC_TOKEN));
        String keystoneToken = userToken.getKeystoneUnscopedToken();
        
        ResponseEntity<JsonNode> response = keystoneProjectAPIModule.getProjectDetail(projectId, keystoneToken);
        if (response == null) {
            throw new KeystoneManagementException(AuthErrorCode.KEYSTONE_PROJECT_RETRIEVAL_FAILED, "프로젝트 조회 응답이 null입니다.");
        }
        return KeystoneAPIUtils.parseKeystoneProjectResponse(response);
    }

    @Transactional
    public KeystoneProject updateProject(String projectId, KeystoneProject project, String requesterId) {
        UserTokenEntity userToken = userTokenRepositoryPort.findByUserIdAndIsActiveTrue(requesterId)
                .orElseThrow(() -> new JwtAuthenticationException(AuthErrorCode.NOT_FOUND_ACC_TOKEN));
        String keystoneToken = userToken.getKeystoneUnscopedToken();
        
        Map<String, Object> projectRequest = KeystoneAPIUtils.createKeystoneUpdateProjectRequest(project);
        ResponseEntity<JsonNode> response = keystoneProjectAPIModule.updateProject(projectId, keystoneToken, projectRequest);
        if (response == null) {
            throw new KeystoneManagementException(AuthErrorCode.KEYSTONE_PROJECT_UPDATE_FAILED, "프로젝트 업데이트 응답이 null입니다.");
        }
        return KeystoneAPIUtils.parseKeystoneProjectResponse(response);
    }

    @Transactional
    public void deleteProject(String projectId, String requesterId) {
        UserTokenEntity userToken = userTokenRepositoryPort.findByUserIdAndIsActiveTrue(requesterId)
                .orElseThrow(() -> new JwtAuthenticationException(AuthErrorCode.NOT_FOUND_ACC_TOKEN));
        String keystoneToken = userToken.getKeystoneUnscopedToken();

        keystoneProjectAPIModule.deleteProject(projectId, keystoneToken);
    }

    public String issueProjectScopeToken(String projectId, String userId) {
        UserTokenEntity userToken = userTokenRepositoryPort.findByUserIdAndIsActiveTrue(userId)
                .orElseThrow(() -> new JwtAuthenticationException(AuthErrorCode.NOT_FOUND_ACC_TOKEN));

        String unscopedToken = userToken.getKeystoneUnscopedToken();

        Map<String, Object> tokenRequest = KeystoneAPIUtils.createProjectScopeTokenRequest(projectId , unscopedToken);
        ResponseEntity<JsonNode> response = keystoneAuthAPIModule.issueProjectScopeToken(tokenRequest);
        if (response == null) {
            throw new KeystoneManagementException(AuthErrorCode.KEYSTONE_TOKEN_EXTRACTION_FAILED, "프로젝트 스코프 토큰 발급 응답이 null입니다.");
        }
        return KeystoneAPIUtils.extractKeystoneToken(response);
    }

    @Transactional
    public String authenticateKeystoneAndGenerateJwt(KeystonePasswordLoginRequest request) {

        Map<String, Object> passwordAuthRequest = KeystoneAPIUtils.createPasswordAuthRequest(request);

        // keystone unscoped token 요청
        ResponseEntity<JsonNode> loginResponse = keystoneAuthAPIModule.issueUnscopedToken(passwordAuthRequest);
        if (loginResponse == null) {
            throw new JwtAuthenticationException(AuthErrorCode.FAILED_TOKEN_AUTHENTICATION);
        }

        // 3. keystone token과 user 정보 추출
        String keystoneToken = KeystoneAPIUtils.extractKeystoneToken(loginResponse);
        KeystoneTokenInfo tokenInfo = KeystoneAPIUtils.extractKeystoneTokenInfo(loginResponse);
        String userId = tokenInfo.userId();

        // 4. 기존 토큰들 비활성화
        userTokenRepositoryPort.deactivateAllByUserId(userId);

        // 5. ACC JWT 토큰 생성
        String jwtToken = jwtUtils.generateToken(userId, keystoneToken);

        // 6. 토큰 엔티티 저장
        LocalDateTime expiresAt = LocalDateTime.now().plusSeconds(86400); // 24시간 기본값
        UserTokenEntity tokenEntity = UserTokenEntity.create(userId, jwtToken, keystoneToken, expiresAt);
        userTokenRepositoryPort.save(tokenEntity);

        return jwtToken;
    }

}
