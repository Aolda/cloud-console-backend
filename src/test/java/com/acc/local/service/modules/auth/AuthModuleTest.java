package com.acc.local.service.modules.auth;

import com.acc.global.security.JwtUtils;
import com.acc.local.domain.model.KeystoneProject;
import com.acc.local.domain.model.User;
import com.acc.local.dto.auth.KeystonePasswordLoginRequest;
import com.acc.local.entity.UserTokenEntity;
import com.acc.local.external.modules.keystone.KeystoneAuthAPIModule;
import com.acc.local.external.modules.keystone.KeystoneProjectAPIModule;
import com.acc.local.external.modules.keystone.KeystoneRoleAPIModule;
import com.acc.local.external.modules.keystone.KeystoneUserAPIModule;
import com.acc.local.repository.ports.UserTokenRepositoryPort;
import com.acc.local.dto.auth.KeystoneTokenInfo;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthModuleTest {

    @Mock
    private WebClient keystoneWebClient;

    @Mock
    private JwtUtils jwtUtils;

    @Mock
    private UserTokenRepositoryPort userTokenRepositoryPort;

    @Mock
    private KeystoneAuthAPIModule keystoneAuthAPIModule;
    @Mock
    private KeystoneProjectAPIModule keystoneProjectAPIModule;
    @Mock
    private KeystoneUserAPIModule keystoneUserAPIModule;
    @Mock
    private KeystoneRoleAPIModule keystoneRoleAPIModule;

    @InjectMocks
    private AuthModule authModule;

    private ObjectMapper objectMapper = new ObjectMapper();

    @Test
    @DisplayName("Keycloak 토큰으로 인증하고 JWT 토큰을 생성할 수 있다")
    void givenKeycloakToken_whenJWTGenerateMethodIsCalled_thenReturnAoldaJWT() throws Exception {
        // Given
        String keycloakToken = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiI1MGIwM2U0Ny14eHh4LXh4eHgteHh4eC14eHh4eHh4eCJ9";
        String userId = "50b03e47-xxxx-xxxx-xxxx-xxxxxxxx";
        String keystoneToken = "14080769fe05e1f8b837fb43ca0f0ba4";
        String expectedJwtToken = "jwt-token-12345";

        // Mock ResponseEntity for login response
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Subject-Token", keystoneToken);
        JsonNode emptyBody = objectMapper.readTree("{\"token\": {\"methods\": [\"password\", \"token\"], \"user\": {\"domain\": {\"id\": \"default\", \"name\": \"Default\"}, \"id\": \"5d8dc5fb0a0b412fa239eade92059b69\", \"name\": \"Acc_test_123\", \"password_expires_at\": null}, \"audit_ids\": [\"zs3FjaQQQ0ikRHBT5g-wTg\", \"acpjPWkbRCW7Pv7nVgUcZA\"], \"expires_at\": \"2025-09-29T12:29:18.000000Z\", \"issued_at\": \"2025-09-28T12:31:32.000000Z\"}}");
        ResponseEntity<JsonNode> loginResponse = new ResponseEntity<>(emptyBody, headers, HttpStatus.OK);

        when(jwtUtils.extractUserIdFromKeycloakToken(keycloakToken)).thenReturn(userId);
        when(keystoneAuthAPIModule.requestFederateLogin(keycloakToken)).thenReturn(loginResponse);
        when(jwtUtils.generateToken(userId, keystoneToken)).thenReturn(expectedJwtToken);
        when(userTokenRepositoryPort.save(any(UserTokenEntity.class))).thenReturn(mock(UserTokenEntity.class));

        // When
        String actualJwtToken = authModule.authenticateAndGenerateJwt(keycloakToken);

        // Then
        assertEquals(expectedJwtToken, actualJwtToken);
        verify(jwtUtils).extractUserIdFromKeycloakToken(keycloakToken);
        verify(userTokenRepositoryPort).deactivateAllByUserId(userId);
        verify(keystoneAuthAPIModule).requestFederateLogin(keycloakToken);
        verify(jwtUtils).generateToken(userId, keystoneToken);
        verify(userTokenRepositoryPort).save(any(UserTokenEntity.class));
    }

    // TODO: 권한 테스트는 별도로 처리 예정
    // @Test
    // @DisplayName("사용자 ID와 프로젝트 ID로 프로젝트 권한을 조회할 수 있다")
    // void givenProjectIdAndUserId_whenGetProjectPermissionMethodIsCalled_thenReturnAoldaProjectPermission() {
    //     // 권한 관련 테스트는 나중에 별도 처리
    // }

    // ==== User CRUD Tests ====

    @Test
    @DisplayName("관리자는 ACC 요청자의 개인정보와 keystone 토큰을 이용해 Keystone의 사용자 계정을 생성할 수 있다.")
    void givenDomainUserAndKeystoneToken_whenCreateKeystoneUser_thenReturnKeystoneUserInfo() throws Exception {
        // given
        User user = User.builder()
                .name("testUser")
                .email("test@example.com")
                .enabled(true)
                .build();
        String userId = "admin-user-id";
        String keystoneToken = "test-keystone-token";
        String createdUserId = "created-user-id";
        
        UserTokenEntity mockUserToken = mock(UserTokenEntity.class);
        when(mockUserToken.getKeystoneUnscopedToken()).thenReturn(keystoneToken);
        when(userTokenRepositoryPort.findByUserIdAndIsActiveTrue(userId)).thenReturn(Optional.of(mockUserToken));
        
        JsonNode userBody = objectMapper.readTree(
            "{\"user\": {\"id\": \"" + createdUserId + "\", \"name\": \"testUser\", \"email\": \"test@example.com\", \"enabled\": true}}"
        );
        ResponseEntity<JsonNode> mockResponse = new ResponseEntity<>(userBody, HttpStatus.OK);
        when(keystoneUserAPIModule.createUser(eq(keystoneToken), any())).thenReturn(mockResponse);

        // when
        User createdUser = authModule.createUser(user, userId);

        // then
        assertEquals(createdUserId, createdUser.getId());
        assertEquals("testUser", createdUser.getName());
        assertEquals("test@example.com", createdUser.getEmail());
        assertTrue(createdUser.isEnabled());
        verify(keystoneUserAPIModule).createUser(eq(keystoneToken), any());
    }

    @Test
    @DisplayName("관리자와 사용자 본인은 keystone 토큰과 사용자 ID를 이용해 Keystone에서 사용자 상세 정보를 조회할 수 있다.")
    void givenUserIdAndKeystoneToken_whenGetUserDetail_thenReturnUserInfo() throws Exception {
        // given
        String targetUserId = "target-user-id";
        String requesterId = "requester-id";
        String keystoneToken = "test-keystone-token";
        
        UserTokenEntity mockUserToken = mock(UserTokenEntity.class);
        when(mockUserToken.getKeystoneUnscopedToken()).thenReturn(keystoneToken);
        when(userTokenRepositoryPort.findByUserIdAndIsActiveTrue(requesterId)).thenReturn(Optional.of(mockUserToken));
        
        JsonNode userBody = objectMapper.readTree(
            "{\"user\": {\"id\": \"" + targetUserId + "\"," +
                    " \"name\": \"testUser\"," +
                    " \"email\": \"test@example.com\", " +
                    "\"enabled\": true, " +
                    "\"description\": \"test description\"}}"
        );
        ResponseEntity<JsonNode> mockResponse = new ResponseEntity<>(userBody, HttpStatus.OK);
        when(keystoneUserAPIModule.getUserDetail(targetUserId, keystoneToken)).thenReturn(mockResponse);

        // when
        User userDetail = authModule.getUserDetail(targetUserId, requesterId);

        // then
        assertEquals(targetUserId, userDetail.getId());
        assertEquals("testUser", userDetail.getName());
        assertEquals("test@example.com", userDetail.getEmail());
        assertEquals("test description", userDetail.getDescription());
        assertTrue(userDetail.isEnabled());
        verify(keystoneUserAPIModule).getUserDetail(targetUserId, keystoneToken);
    }

    @Test
    @DisplayName("관리자와 사용자 본인은 keystone 토큰과 사용자 ID, 수정할 정보를 이용해 Keystone에서 사용자 정보를 업데이트할 수 있다.")
    void givenUserIdAndUserInfoAndKeystoneToken_whenUpdateUser_thenReturnUpdatedUserInfo() throws Exception {
        // given
        String targetUserId = "target-user-id";
        String requesterId = "requester-id";
        User user = User.builder()
                .name("updatedUser")
                .email("updated@example.com")
                .description("updated description")
                .enabled(true)
                .build();
        String keystoneToken = "test-keystone-token";
        
        UserTokenEntity mockUserToken = mock(UserTokenEntity.class);
        when(mockUserToken.getKeystoneUnscopedToken()).thenReturn(keystoneToken);
        when(userTokenRepositoryPort.findByUserIdAndIsActiveTrue(requesterId)).thenReturn(Optional.of(mockUserToken));
        
        JsonNode userBody = objectMapper.readTree(
            "{\"user\": {\"id\": \"" + targetUserId + "\"," +
                    " \"name\": \"updatedUser\", " +
                    "\"email\": \"updated@example.com\", " +
                    "\"description\": \"updated description\", " +
                    "\"enabled\": true}}"
        );
        ResponseEntity<JsonNode> mockResponse = new ResponseEntity<>(userBody, HttpStatus.OK);
        when(keystoneUserAPIModule.updateUser(eq(targetUserId), eq(keystoneToken), any())).thenReturn(mockResponse);

        // when
        User updatedUser = authModule.updateUser(targetUserId, user, requesterId);

        // then
        assertEquals(targetUserId, updatedUser.getId());
        assertEquals("updatedUser", updatedUser.getName());
        assertEquals("updated@example.com", updatedUser.getEmail());
        assertEquals("updated description", updatedUser.getDescription());
        assertTrue(updatedUser.isEnabled());
        verify(keystoneUserAPIModule).updateUser(eq(targetUserId), eq(keystoneToken), any());
    }

    @Test
    @DisplayName("관리자와 사용자 본인은 keystone 토큰과 사용자 ID를 이용해 Keystone에서 사용자를 삭제할 수 있다.")
    void givenUserIdAndKeystoneToken_whenDeleteUser_thenDeleteUserSuccessfully() {
        // given
        String targetUserId = "target-user-id";
        String requesterId = "requester-id";
        String keystoneToken = "test-keystone-token";
        
        UserTokenEntity mockUserToken = mock(UserTokenEntity.class);
        when(mockUserToken.getKeystoneUnscopedToken()).thenReturn(keystoneToken);
        when(userTokenRepositoryPort.findByUserIdAndIsActiveTrue(requesterId)).thenReturn(Optional.of(mockUserToken));

        // when & then
        assertDoesNotThrow(() -> {
            authModule.deleteUser(targetUserId, requesterId);
        });
        
        verify(keystoneUserAPIModule).deleteUser(targetUserId, keystoneToken);
    }

    // ==== Project CRUD Tests ====

    @Test
    @DisplayName("관리자는 ACC 요청자의 프로젝트 정보와 keystone 토큰을 이용해 Keystone의 프로젝트를 생성할 수 있다.")
    void givenKeystoneProjectAndKeystoneToken_whenCreateKeystoneProject_thenReturnKeystoneProjectInfo() throws Exception {
        // given
        KeystoneProject project = KeystoneProject.builder()
                .name("testProject")
                .description("test project description")
                .enabled(true)
                .isDomain(false)
                .build();
        String userId = "admin-user-id";
        String keystoneToken = "test-keystone-token";
        String createdProjectId = "created-project-id";
        
        UserTokenEntity mockUserToken = mock(UserTokenEntity.class);
        when(mockUserToken.getKeystoneUnscopedToken()).thenReturn(keystoneToken);
        when(userTokenRepositoryPort.findByUserIdAndIsActiveTrue(userId)).thenReturn(Optional.of(mockUserToken));
        
        JsonNode projectBody = objectMapper.readTree(
            "{\"project\": {\"id\": \"" + createdProjectId + "\", \"name\": \"testProject\", \"description\": \"test project description\", \"enabled\": true, \"is_domain\": false}}"
        );
        ResponseEntity<JsonNode> mockResponse = new ResponseEntity<>(projectBody, HttpStatus.OK);
        when(keystoneProjectAPIModule.createProject(eq(keystoneToken), any())).thenReturn(mockResponse);

        // when
        KeystoneProject createdProject = authModule.createProject(project, userId);

        // then
        assertEquals(createdProjectId, createdProject.getId());
        assertEquals("testProject", createdProject.getName());
        assertEquals("test project description", createdProject.getDescription());
        assertTrue(createdProject.getEnabled());
        assertFalse(createdProject.getIsDomain());
        verify(keystoneProjectAPIModule).createProject(eq(keystoneToken), any());
    }

    @Test
    @DisplayName("관리자와 사용자는 keystone 토큰과 프로젝트 ID를 이용해 Keystone에서 프로젝트 상세 정보를 조회할 수 있다.")
    void givenProjectIdAndKeystoneToken_whenGetProjectDetail_thenReturnProjectInfo() throws Exception {
        // given
        String projectId = "test-project-id";
        String requesterId = "requester-id";
        String keystoneToken = "test-keystone-token";
        
        UserTokenEntity mockUserToken = mock(UserTokenEntity.class);
        when(mockUserToken.getKeystoneUnscopedToken()).thenReturn(keystoneToken);
        when(userTokenRepositoryPort.findByUserIdAndIsActiveTrue(requesterId)).thenReturn(Optional.of(mockUserToken));
        
        JsonNode projectBody = objectMapper.readTree(
            "{\"project\": {\"id\": \"" + projectId + "\"," +
                    " \"name\": \"testProject\"," +
                    " \"description\": \"test project description\", " +
                    "\"enabled\": true, " +
                    "\"is_domain\": false}}"
        );
        ResponseEntity<JsonNode> mockResponse = new ResponseEntity<>(projectBody, HttpStatus.OK);
        when(keystoneProjectAPIModule.getProjectDetail(projectId, keystoneToken)).thenReturn(mockResponse);

        // when
        KeystoneProject projectDetail = authModule.getProjectDetail(projectId, requesterId);

        // then
        assertEquals(projectId, projectDetail.getId());
        assertEquals("testProject", projectDetail.getName());
        assertEquals("test project description", projectDetail.getDescription());
        assertTrue(projectDetail.getEnabled());
        assertFalse(projectDetail.getIsDomain());
        verify(keystoneProjectAPIModule).getProjectDetail(projectId, keystoneToken);
    }

    @Test
    @DisplayName("관리자는 keystone 토큰과 프로젝트 ID, 수정할 정보를 이용해 Keystone에서 프로젝트 정보를 업데이트할 수 있다.")
    void givenProjectIdAndProjectInfoAndKeystoneToken_whenUpdateProject_thenReturnUpdatedProjectInfo() throws Exception {
        // given
        String projectId = "test-project-id";
        String requesterId = "requester-id";
        KeystoneProject project = KeystoneProject.builder()
                .name("updatedProject")
                .description("updated project description")
                .enabled(true)
                .isDomain(false)
                .build();
        String keystoneToken = "test-keystone-token";
        
        UserTokenEntity mockUserToken = mock(UserTokenEntity.class);
        when(mockUserToken.getKeystoneUnscopedToken()).thenReturn(keystoneToken);
        when(userTokenRepositoryPort.findByUserIdAndIsActiveTrue(requesterId)).thenReturn(Optional.of(mockUserToken));
        
        JsonNode projectBody = objectMapper.readTree(
            "{\"project\": {\"id\": \"" + projectId + "\"," +
                    " \"name\": \"updatedProject\", " +
                    "\"description\": \"updated project description\", " +
                    "\"enabled\": true, " +
                    "\"is_domain\": false}}"
        );
        ResponseEntity<JsonNode> mockResponse = new ResponseEntity<>(projectBody, HttpStatus.OK);
        when(keystoneProjectAPIModule.updateProject(eq(projectId), eq(keystoneToken), any())).thenReturn(mockResponse);

        // when
        KeystoneProject updatedProject = authModule.updateProject(projectId, project, requesterId);

        // then
        assertEquals(projectId, updatedProject.getId());
        assertEquals("updatedProject", updatedProject.getName());
        assertEquals("updated project description", updatedProject.getDescription());
        assertTrue(updatedProject.getEnabled());
        assertFalse(updatedProject.getIsDomain());
        verify(keystoneProjectAPIModule).updateProject(eq(projectId), eq(keystoneToken), any());
    }

    @Test
    @DisplayName("관리자는 keystone 토큰과 프로젝트 ID를 이용해 Keystone에서 프로젝트를 삭제할 수 있다.")
    void givenProjectIdAndKeystoneToken_whenDeleteProject_thenDeleteProjectSuccessfully() {
        // given
        String projectId = "test-project-id";
        String requesterId = "requester-id";
        String keystoneToken = "test-keystone-token";
        
        UserTokenEntity mockUserToken = mock(UserTokenEntity.class);
        when(mockUserToken.getKeystoneUnscopedToken()).thenReturn(keystoneToken);
        when(userTokenRepositoryPort.findByUserIdAndIsActiveTrue(requesterId)).thenReturn(Optional.of(mockUserToken));

        // when & then
        assertDoesNotThrow(() -> {
            authModule.deleteProject(projectId, requesterId);
        });

        verify(keystoneProjectAPIModule).deleteProject(projectId, keystoneToken);
    }

    @Test
    @DisplayName("사용자 ID와 프로젝트 ID를 이용해 프로젝트 스코프 토큰을 발급할 수 있다.")
    void givenProjectIdAndUserId_whenIssueProjectScopeToken_thenReturnProjectScopeToken() throws Exception {
        // given
        String projectId = "test-project-id";
        String userId = "test-user-id";
        String unscopedToken = "unscoped-keystone-token";
        String expectedProjectScopeToken = "project-scoped-token";

        UserTokenEntity mockUserToken = mock(UserTokenEntity.class);
        when(mockUserToken.getKeystoneUnscopedToken()).thenReturn(unscopedToken);
        when(userTokenRepositoryPort.findByUserIdAndIsActiveTrue(userId)).thenReturn(Optional.of(mockUserToken));

        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Subject-Token", expectedProjectScopeToken);
        JsonNode emptyBody = objectMapper.readTree("{}");
        ResponseEntity<JsonNode> mockResponse = new ResponseEntity<>(emptyBody, headers, HttpStatus.OK);
        when(keystoneAuthAPIModule.issueProjectScopeToken(any())).thenReturn(mockResponse);

        // when
        String actualToken = authModule.issueProjectScopeToken(projectId, userId);

        // then
        assertEquals(expectedProjectScopeToken, actualToken);
        verify(userTokenRepositoryPort).findByUserIdAndIsActiveTrue(userId);
        verify(keystoneAuthAPIModule).issueProjectScopeToken(any());
    }

    @Test
    @DisplayName("Keystone 패스워드 로그인 요청으로 인증하고 JWT 토큰을 생성할 수 있다.")
    void givenKeystonePasswordLoginRequest_whenAuthenticateKeystoneAndGenerateJwt_thenReturnJwtToken() throws Exception {
        // given
        KeystonePasswordLoginRequest request = new KeystonePasswordLoginRequest(
            "testuser",
            "testpassword",
            "Default"
        );
        String keystoneToken = "keystone-unscoped-token";
        String userId = "test-user-id";
        String expectedJwtToken = "jwt-token";

        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Subject-Token", keystoneToken);
        JsonNode responseBody = objectMapper.readTree(
            "{\"token\": {" +
                "\"expires_at\": \"2024-12-31T23:59:59.000000Z\"," +
                "\"issued_at\": \"2024-01-01T00:00:00.000000Z\"," +
                "\"audit_ids\": [\"audit1\", \"audit2\"]," +
                "\"user\": {" +
                    "\"id\": \"" + userId + "\"," +
                    "\"name\": \"testuser\"," +
                    "\"domain\": {\"id\": \"default\", \"name\": \"Default\"}" +
                "}" +
            "}}"
        );
        ResponseEntity<JsonNode> loginResponse = new ResponseEntity<>(responseBody, headers, HttpStatus.OK);

        when(keystoneAuthAPIModule.issueUnscopedToken(any())).thenReturn(loginResponse);
        when(jwtUtils.generateToken(userId, keystoneToken)).thenReturn(expectedJwtToken);
        when(userTokenRepositoryPort.save(any(UserTokenEntity.class))).thenReturn(mock(UserTokenEntity.class));

        // when
        String actualJwtToken = authModule.authenticateKeystoneAndGenerateJwt(request);

        // then
        assertEquals(expectedJwtToken, actualJwtToken);
        verify(keystoneAuthAPIModule).issueUnscopedToken(any());
        verify(userTokenRepositoryPort).deactivateAllByUserId(userId);
        verify(jwtUtils).generateToken(userId, keystoneToken);
        verify(userTokenRepositoryPort).save(any(UserTokenEntity.class));
    }
}
