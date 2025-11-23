package com.acc.local.service.modules.auth;

import com.acc.global.properties.OpenstackProperties;
import com.acc.local.domain.enums.auth.AuthType;
import com.acc.local.domain.enums.auth.KeystoneTokenType;
import com.acc.global.security.jwt.JwtUtils;
import com.acc.local.domain.model.auth.RefreshToken;
import com.acc.local.dto.project.UpdateProjectRequest;
import com.acc.local.domain.model.auth.KeystoneUser;
import com.acc.local.domain.model.auth.UserToken;
import com.acc.local.dto.auth.KeystonePasswordLoginRequest;
import com.acc.local.dto.auth.KeystoneToken;
import com.acc.local.dto.auth.SignupRequest;
import com.acc.local.entity.RefreshTokenEntity;
import com.acc.local.entity.UserAuthDetailEntity;
import com.acc.local.entity.UserDetailEntity;
import com.acc.local.entity.UserTokenEntity;
import com.acc.local.external.dto.keystone.KeystoneProject;
import com.acc.local.external.modules.keystone.KeystoneAPIUtils;
import com.acc.local.external.ports.KeystoneAPIExternalPort;
import com.acc.local.repository.ports.UserTokenRepositoryPort;
import com.fasterxml.jackson.core.JsonProcessingException;
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

import java.security.InvalidParameterException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

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
    private KeystoneAPIExternalPort keystoneAPIExternalPort;

    @Mock
    private OpenstackProperties openstackProperties;

    @Mock
    private com.acc.local.repository.ports.RefreshTokenRepositoryPort refreshTokenRepositoryPort;

    @Mock
    private com.acc.local.repository.ports.UserRepositoryPort userRepositoryPort;

    @InjectMocks
    private AuthModule authModule;

    @InjectMocks
    private ProjectModule projectModule;

    private ObjectMapper objectMapper = new ObjectMapper();

    // =========== | Util Methods | ============
    private void mockSystemAdminEnv(String testSaUsername, String testSaPassword) {
        when(openstackProperties.getSaUsername()).thenReturn(testSaUsername);
        when(openstackProperties.getSaPassword()).thenReturn(testSaPassword);
    }

    private void mockSystemAdminScopedRequest(String returnScopedToken, String unscopedToken) throws JsonProcessingException {
        when(keystoneAPIExternalPort.getAdminToken(any())).thenReturn(new KeystoneToken(
            KeystoneTokenType.SCOPED,
            List.of("auditId-1234", "auditid-12345"),
            LocalDateTime.now().plusDays(2),
            LocalDateTime.now(),
            "test-admin-id",
            "test-admin",
            returnScopedToken,
            true
        ));
        // // return examples
        // HttpHeaders issueSystemScopeHeader = new HttpHeaders();
        // issueSystemScopeHeader.set("X-Subject-Token", returnScopedToken);
        //
        // JsonNode issueSystemScopeBody = objectMapper.readTree("{\"token\": {\"methods\": [\"password\", \"token\"], \"user\": {\"domain\": {\"id\": \"default\", \"name\": \"Default\"}, \"id\": \"5d8dc5fb0a0b412fa239eade92059b69\", \"name\": \"Acc_test_123\", \"password_expires_at\": null}, \"audit_ids\": [\"zs3FjaQQQ0ikRHBT5g-wTg\", \"acpjPWkbRCW7Pv7nVgUcZA\"], \"expires_at\": \"2025-09-29T12:29:18.000000Z\", \"issued_at\": \"2025-09-28T12:31:32.000000Z\"}}");
        //
        // // request template
        // Map<String, Object> scopeTokenRequest = KeystoneAPIUtils.createSystemAdminTokenRequest(unscopedToken);
        //
        // // request mock
        // mockMethodExternalRequest(
        //     keystoneAuthAPIModule.issueScopeToken(scopeTokenRequest),
        //     issueSystemScopeHeader,
        //     issueSystemScopeBody
        // );
    }

    public void mockScopedRequest(String returnScopedToken, String projectId, String unscopedToken) throws JsonProcessingException {
        // return examples
        HttpHeaders issueScopedTokenHeader = new HttpHeaders();
        issueScopedTokenHeader.set("X-Subject-Token", returnScopedToken);

        JsonNode issueScopedBody = objectMapper.readTree("{\"token\": {\"methods\": [\"password\", \"token\"], \"user\": {\"domain\": {\"id\": \"default\", \"name\": \"Default\"}, \"id\": \"5d8dc5fb0a0b412fa239eade92059b69\", \"name\": \"Acc_test_123\", \"password_expires_at\": null}, \"audit_ids\": [\"zs3FjaQQQ0ikRHBT5g-wTg\", \"acpjPWkbRCW7Pv7nVgUcZA\"], \"expires_at\": \"2025-09-29T12:29:18.000000Z\", \"issued_at\": \"2025-09-28T12:31:32.000000Z\"}}");

        // request template
        Map<String, Object> scopedTokenRequest = KeystoneAPIUtils.createProjectScopeTokenRequest(projectId, unscopedToken);

        // request mock
        mockMethodExternalRequest(
            keystoneAPIExternalPort.issueScopedToken(scopedTokenRequest),
            issueScopedTokenHeader,
            issueScopedBody
        );
    }

    public void mockUnscopedRequest(String returnUnscopedToken, String username, String password) throws JsonProcessingException {
        when(keystoneAPIExternalPort.getAdminToken(any())).thenReturn(new KeystoneToken(
            KeystoneTokenType.UNSCOPED,
            List.of("auditId-1234", "auditid-12345"),
            LocalDateTime.now().plusDays(2),
            LocalDateTime.now(),
            "test-admin-id",
            username,
            returnUnscopedToken,
            false
        ));
        // // return examples
        // HttpHeaders issueUnscopeTokenHeader = new HttpHeaders();
        // issueUnscopeTokenHeader.set("X-Subject-Token", returnUnscopedToken);
        //
        // JsonNode issueUnscopeBody = objectMapper.readTree("{\"token\": {\"methods\": [\"password\", \"token\"], \"user\": {\"domain\": {\"id\": \"default\", \"name\": \"Default\"}, \"id\": \"5d8dc5fb0a0b412fa239eade92059b69\", \"name\": \"Acc_test_123\", \"password_expires_at\": null}, \"audit_ids\": [\"zs3FjaQQQ0ikRHBT5g-wTg\", \"acpjPWkbRCW7Pv7nVgUcZA\"], \"expires_at\": \"2025-09-29T12:29:18.000000Z\", \"issued_at\": \"2025-09-28T12:31:32.000000Z\"}}");
        //
        // // request template
        // Map<String, Object> unscopeTokenRequest = KeystoneAPIUtils.createPasswordAuthRequest(
        //     new KeystonePasswordLoginRequest(
        //         username, password, "Default"
        //     )
        // );
        //
        // // request mock
        // mockMethodExternalRequest(
        //     keystoneAuthAPIModule.issueUnscopedToken(unscopeTokenRequest),
        //     issueUnscopeTokenHeader,
        //     issueUnscopeBody
        // );
    }

    public <T extends ResponseEntity<JsonNode>> void mockMethodExternalRequest(T jsonNodeResponseEntity, HttpHeaders header, JsonNode body) {
        T loginResponse = (T) new ResponseEntity<>(body, header, HttpStatus.OK);
        when(jsonNodeResponseEntity).thenReturn(loginResponse);
    }

    // ============== | Test Methods | ================

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
        JsonNode emptyBody = objectMapper.readTree("{\"token\": {\"methods\": [\"password\", \"token\"], \"user\": {\"domain\": {\"id\": \"default\", \"name\": \"Default\"}, \"id\": \"50b03e47-xxxx-xxxx-xxxx-xxxxxxxx\", \"name\": \"Acc_test_123\", \"password_expires_at\": null}, \"audit_ids\": [\"zs3FjaQQQ0ikRHBT5g-wTg\", \"acpjPWkbRCW7Pv7nVgUcZA\"], \"expires_at\": \"2025-09-29T12:29:18.000000Z\", \"issued_at\": \"2025-09-28T12:31:32.000000Z\"}}");
        ResponseEntity<JsonNode> loginResponse = new ResponseEntity<>(emptyBody, headers, HttpStatus.OK);
        
        when(userTokenRepositoryPort.findAllByUserIdAndIsActiveTrue(userId)).thenReturn(List.of());
        when(keystoneAPIExternalPort.requestFederateLogin(keycloakToken)).thenReturn(loginResponse);
        when(jwtUtils.generateToken(userId)).thenReturn(expectedJwtToken);
        when(userTokenRepositoryPort.save(any(UserTokenEntity.class))).thenReturn(mock(UserTokenEntity.class));

        // When
        String actualJwtToken = authModule.authenticateAndGenerateJwt(keycloakToken);

        // Then
        assertEquals(expectedJwtToken, actualJwtToken);
        verify(userTokenRepositoryPort).deactivateAllByUserId(userId);
        verify(keystoneAPIExternalPort).requestFederateLogin(keycloakToken);
        verify(jwtUtils).generateToken(userId);
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
    @DisplayName("관리자는 ACC 요청자의 개인정보와 keystone 토큰을 이용해 Keystone의 사용자 계정을 생성하고 ACC DB에 저장할 수 있다.")
    void givenDomainUserAndKeystoneToken_whenCreateKeystoneUser_thenReturnKeystoneUserInfoAndSaveToAccDB() throws Exception {
        // given
        KeystoneUser keystoneUser = KeystoneUser.builder()
                .name("testUser")
                .email("test@example.com")
                .enabled(true)
                .department("컴퓨터공학과")
                .phoneNumber("010-1234-5678")
                .build();
        String userId = "admin-user-id";
        String keystoneToken = "test-keystone-token";
        String createdUserId = "created-user-id";

        UserTokenEntity mockUserToken = mock(UserTokenEntity.class);
        when(mockUserToken.getKeystoneUnscopedToken()).thenReturn(keystoneToken);
        when(userTokenRepositoryPort.findAllByUserIdAndIsActiveTrue(userId)).thenReturn(List.of(mockUserToken));

        JsonNode userBody = objectMapper.readTree(
            "{\"user\": {\"id\": \"" + createdUserId + "\", \"name\": \"testUser\", \"email\": \"test@example.com\", \"enabled\": true, \"domain_id\": \"default\"}}"
        );
        ResponseEntity<JsonNode> mockResponse = new ResponseEntity<>(userBody, HttpStatus.OK);
        when(keystoneAPIExternalPort.createUser(eq(keystoneToken), any())).thenReturn(mockResponse);
        // when
        KeystoneUser createdKeystoneUser = authModule.createUser(keystoneUser, userId);

        // then
        assertEquals(createdUserId, createdKeystoneUser.getId());
        assertEquals("testUser", createdKeystoneUser.getName());
        assertEquals("test@example.com", createdKeystoneUser.getEmail());
        assertTrue(createdKeystoneUser.isEnabled());
        assertEquals("컴퓨터공학과", createdKeystoneUser.getDepartment());
        assertEquals("010-1234-5678", createdKeystoneUser.getPhoneNumber());
        verify(keystoneAPIExternalPort).createUser(eq(keystoneToken), any());
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

        when(userTokenRepositoryPort.findAllByUserIdAndIsActiveTrue(requesterId)).thenReturn(List.of(mockUserToken));

        JsonNode userBody = objectMapper.readTree(
            "{\"user\": {\"id\": \"" + targetUserId + "\"," +
                    " \"name\": \"testUser\"," +
                    " \"email\": \"test@example.com\", " +
                    "\"enabled\": true, " +
                    "\"description\": \"test description\"}}"
        );
        ResponseEntity<JsonNode> mockResponse = new ResponseEntity<>(userBody, HttpStatus.OK);
        when(keystoneAPIExternalPort.getUserDetail(targetUserId, keystoneToken)).thenReturn(mockResponse);
        
        // when
        KeystoneUser keystoneUserDetail = authModule.getUserDetail(targetUserId, requesterId);

        // then
        assertEquals(targetUserId, keystoneUserDetail.getId());
        assertEquals("testUser", keystoneUserDetail.getName());
        assertEquals("test@example.com", keystoneUserDetail.getEmail());
        assertEquals("test description", keystoneUserDetail.getDescription());
        assertTrue(keystoneUserDetail.isEnabled());
        verify(keystoneAPIExternalPort).getUserDetail(targetUserId, keystoneToken);
       }

    @Test
    @DisplayName("관리자와 사용자 본인은 keystone 토큰과 사용자 ID, 수정할 정보를 이용해 Keystone에서 사용자 정보를 업데이트할 수 있다.")
    void givenUserIdAndUserInfoAndKeystoneToken_whenUpdateUser_thenReturnUpdatedUserInfo() throws Exception {
        // given
        String targetUserId = "target-user-id";
        String requesterId = "requester-id";
        KeystoneUser keystoneUser = KeystoneUser.builder()
                .name("updatedUser")
                .email("updated@example.com")
                .description("updated description")
                .enabled(true)
                .department("전자공학과")
                .phoneNumber("010-9876-5432")
                .build();
        String keystoneToken = "test-keystone-token";

        UserTokenEntity mockUserToken = mock(UserTokenEntity.class);
        when(mockUserToken.getKeystoneUnscopedToken()).thenReturn(keystoneToken);
        when(userTokenRepositoryPort.findAllByUserIdAndIsActiveTrue(requesterId)).thenReturn(List.of(mockUserToken));

        JsonNode userBody = objectMapper.readTree(
            "{\"user\": {\"id\": \"" + targetUserId + "\"," +
                    " \"name\": \"updatedUser\", " +
                    "\"email\": \"updated@example.com\", " +
                    "\"description\": \"updated description\", " +
                    "\"enabled\": true}}"
        );
        ResponseEntity<JsonNode> mockResponse = new ResponseEntity<>(userBody, HttpStatus.OK);
        when(keystoneAPIExternalPort.updateUser(eq(targetUserId), eq(keystoneToken), any())).thenReturn(mockResponse);
        KeystoneUser updatedKeystoneUser = authModule.updateUser(targetUserId, keystoneUser, requesterId);

        // then
        assertEquals(targetUserId, updatedKeystoneUser.getId());
        assertEquals("updatedUser", updatedKeystoneUser.getName());
        assertEquals("updated@example.com", updatedKeystoneUser.getEmail());
        assertEquals("updated description", updatedKeystoneUser.getDescription());
        assertTrue(updatedKeystoneUser.isEnabled());
        assertEquals("전자공학과", updatedKeystoneUser.getDepartment());
        assertEquals("010-9876-5432", updatedKeystoneUser.getPhoneNumber());
        verify(keystoneAPIExternalPort).updateUser(eq(targetUserId), eq(keystoneToken), any());
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
        when(userTokenRepositoryPort.findAllByUserIdAndIsActiveTrue(requesterId)).thenReturn(List.of(mockUserToken));

        // when & then
        assertDoesNotThrow(() -> {
            authModule.deleteUser(targetUserId, requesterId);
        });

        verify(keystoneAPIExternalPort).deleteUser(targetUserId, keystoneToken);
    }

    @Test
    @DisplayName("이메일로 사용자 존재 여부를 확인할 수 있다.")
    void givenEmail_whenIsUserExistsByEmail_thenReturnBooleanResult() {
        // given
        String existingEmail = "existing@example.com";
        String nonExistingEmail = "nonexisting@example.com";

        // when
        boolean existingResult = authModule.isUserExistsByEmail(existingEmail);
        boolean nonExistingResult = authModule.isUserExistsByEmail(nonExistingEmail);

        // then
        assertFalse(nonExistingResult);
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
        when(userTokenRepositoryPort.findAllByUserIdAndIsActiveTrue(userId)).thenReturn(List.of(mockUserToken));

        JsonNode projectBody = objectMapper.readTree(
            "{\"project\": {\"id\": \"" + createdProjectId + "\", \"name\": \"testProject\", \"description\": \"test project description\", \"enabled\": true, \"is_domain\": false}}"
        );
        ResponseEntity<JsonNode> mockResponse = new ResponseEntity<>(projectBody, HttpStatus.OK);
        when(keystoneAPIExternalPort.createProject(eq(keystoneToken), any())).thenReturn(mockResponse);

        // when
        KeystoneProject createdProject = projectModule.createProject(keystoneToken, any(), userId);
        // KeystoneProject createdProject = authModule.createProject(keystoneToken, project, userId);

        // then
        assertEquals(createdProjectId, createdProject.getId());
        assertEquals("testProject", createdProject.getName());
        assertEquals("test project description", createdProject.getDescription());
        assertTrue(createdProject.getEnabled());
        assertFalse(createdProject.getIsDomain());
        verify(keystoneAPIExternalPort).createProject(eq(keystoneToken), any());
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
        when(userTokenRepositoryPort.findAllByUserIdAndIsActiveTrue(requesterId)).thenReturn(List.of(mockUserToken));

        JsonNode projectBody = objectMapper.readTree(
            "{\"project\": {\"id\": \"" + projectId + "\"," +
                    " \"name\": \"testProject\"," +
                    " \"description\": \"test project description\", " +
                    "\"enabled\": true, " +
                    "\"is_domain\": false}}"
        );
        ResponseEntity<JsonNode> mockResponse = new ResponseEntity<>(projectBody, HttpStatus.OK);
        when(keystoneAPIExternalPort.getProjectDetail(projectId, keystoneToken)).thenReturn(mockResponse);

        // when
        KeystoneProject projectDetail = projectModule.getProjectDetail(projectId, requesterId);

        // then
        assertEquals(projectId, projectDetail.getId());
        assertEquals("testProject", projectDetail.getName());
        assertEquals("test project description", projectDetail.getDescription());
        assertTrue(projectDetail.getEnabled());
        assertFalse(projectDetail.getIsDomain());
        verify(keystoneAPIExternalPort).getProjectDetail(projectId, keystoneToken);
    }

    @Test
    @DisplayName("관리자는 keystone 토큰과 프로젝트 ID, 수정할 정보를 이용해 Keystone에서 프로젝트 정보를 업데이트할 수 있다.")
    void givenProjectIdAndProjectInfoAndKeystoneToken_whenUpdateProject_thenReturnUpdatedProjectInfo() throws Exception {
        // given
        String projectId = "test-project-id";
        String requesterId = "requester-id";
        UpdateProjectRequest updatedProjectRequeest = UpdateProjectRequest.builder()
            .name("updatedProject")
            .description("updated project description")
            .build();
        String keystoneToken = "test-keystone-token";

        UserTokenEntity mockUserToken = mock(UserTokenEntity.class);
        when(mockUserToken.getKeystoneUnscopedToken()).thenReturn(keystoneToken);
        when(userTokenRepositoryPort.findAllByUserIdAndIsActiveTrue(requesterId)).thenReturn(List.of(mockUserToken));

        JsonNode projectBody = objectMapper.readTree(
            "{\"project\": {\"id\": \"" + projectId + "\"," +
                    " \"name\": \"updatedProject\", " +
                    "\"description\": \"updated project description\", " +
                    "\"enabled\": true, " +
                    "\"is_domain\": false}}"
        );
        ResponseEntity<JsonNode> mockResponse = new ResponseEntity<>(projectBody, HttpStatus.OK);
        when(keystoneAPIExternalPort.updateProject(eq(projectId), eq(keystoneToken), any())).thenReturn(mockResponse);

        // when
        KeystoneProject updatedProject = projectModule.updateProject(projectId, updatedProjectRequeest, requesterId);

        // then
        assertEquals(projectId, updatedProject.getId());
        assertEquals("updatedProject", updatedProject.getName());
        assertEquals("updated project description", updatedProject.getDescription());
        assertTrue(updatedProject.getEnabled());
        assertFalse(updatedProject.getIsDomain());
        verify(keystoneAPIExternalPort).updateProject(eq(projectId), eq(keystoneToken), any());
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
        when(userTokenRepositoryPort.findAllByUserIdAndIsActiveTrue(requesterId)).thenReturn(List.of(mockUserToken));

        // when & then
        assertDoesNotThrow(() -> {
            projectModule.deleteProject(projectId, requesterId);
        });

        verify(keystoneAPIExternalPort).deleteProject(projectId, keystoneToken);
    }

    // ==== Token Method Tests ====

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
        when(userTokenRepositoryPort.findAllByUserIdAndIsActiveTrue(userId)).thenReturn(List.of(mockUserToken));

        when(keystoneAPIExternalPort.getScopedToken(projectId, unscopedToken)).thenReturn(new KeystoneToken(
            null,
            null,
            null,
            null,
            null,
            null,
            expectedProjectScopeToken,
            null
        ));

        // when
        String actualToken = authModule.issueProjectScopeToken(projectId, userId);

        // then
        assertEquals(expectedProjectScopeToken, actualToken);
        verify(userTokenRepositoryPort).findAllByUserIdAndIsActiveTrue(userId);
        verify(keystoneAPIExternalPort).getScopedToken(projectId, unscopedToken);
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

        when(keystoneAPIExternalPort.getUnscopedToken(request)).thenReturn(new KeystoneToken(
            KeystoneTokenType.UNSCOPED,
            null,
            LocalDateTime.now().plusSeconds(36400),
            LocalDateTime.now(),
            userId,
            null,
            keystoneToken,
            null
        ));

        // HttpHeaders headers = new HttpHeaders();
        // headers.set("X-Subject-Token", keystoneToken);
        // JsonNode responseBody = objectMapper.readTree(
        //     "{\"token\": {" +
        //         "\"expires_at\": \"2024-12-31T23:59:59.000000Z\"," +
        //         "\"issued_at\": \"2024-01-01T00:00:00.000000Z\"," +
        //         "\"audit_ids\": [\"audit1\", \"audit2\"]," +
        //         "\"user\": {" +
        //             "\"id\": \"" + userId + "\"," +
        //             "\"name\": \"testuser\"," +
        //             "\"domain\": {\"id\": \"default\", \"name\": \"Default\"}" +
        //         "}" +
        //     "}}"
        // );
        // ResponseEntity<JsonNode> loginResponse = new ResponseEntity<>(responseBody, headers, HttpStatus.OK);
        //
        // when(keystoneAuthAPIModule.issueUnscopedToken(any())).thenReturn(loginResponse);
        when(jwtUtils.generateToken(userId)).thenReturn(expectedJwtToken);
        when(userTokenRepositoryPort.save(any(UserTokenEntity.class))).thenReturn(mock(UserTokenEntity.class));

        // when
        String actualJwtToken = authModule.authenticateKeystoneAndGenerateJwt(request);

        // then
        assertEquals(expectedJwtToken, actualJwtToken);
    }

    @Test
    @DisplayName("사용자는 특정 기능의 이용을 위해 사용자 ID를 이용하여 시스템 관리자 토큰을 얻을 수 있다.")
    void givenUserId_whenIssueSystemAdminToken_thenReturnSystemAdminToken() throws Exception {
        // Given
        String userId = "50b03e47-xxxx-xxxx-xxxx-xxxxxxxx";

        String testSaUsername = "testAdminId";
        String testSaPassword = "testAdminPw";
        mockSystemAdminEnv(testSaUsername, testSaPassword);

        // Mock ResponseEntity for login response
        String expectedSystemAdminUnscopedToken = "system-admin-unscoped-token";
        mockUnscopedRequest(expectedSystemAdminUnscopedToken, testSaUsername, testSaPassword);

        String expectedSystemAdminScopedToken = "system-admin-scoped-token";
        mockSystemAdminScopedRequest(expectedSystemAdminScopedToken, expectedSystemAdminUnscopedToken);

        // When
        String actualSystemAdminScopedToken = authModule.issueSystemAdminToken(userId);

        // Then
        assertEquals(expectedSystemAdminScopedToken, actualSystemAdminScopedToken);
    }

    @Test
    @DisplayName("시스템은 메서드 실행을 위해 발행한 시스템 관리자 토큰을 사용 직후 말소시킬 수 있다.")
    void givenSystemAdminToken_whenSystemAdminTokenUseComplete_thenInvalidateSystemAdminToken() throws Exception {
        // Given
        String testSystemAdminScopedToken = "system-admin-scoped-token";

        when(keystoneAPIExternalPort.getTokenObject(testSystemAdminScopedToken)).thenReturn(new KeystoneToken(
            KeystoneTokenType.SCOPED,
            List.of("audit-id-000", "audit-id-001"),
            LocalDateTime.now().plusDays(2),
            LocalDateTime.now(),
            "test-admin-id",
            "test-admin",
            testSystemAdminScopedToken,
            true
        ));
        // when(keystoneAuthAPIModule.revokeToken(testSystemAdminScopedToken)).thenReturn(null);
        // mockMethodExternalRequest(
        //     keystoneAuthAPIModule.getTokenInfo(testSystemAdminScopedToken),
        //     new HttpHeaders(),
        //     objectMapper.readTree("{\"token\": {\"methods\": [\"password\",\"token\"],\"user\": {\"domain\": {\"id\": \"default\",\"name\": \"Default\"},\"id\": \"c0f232df5a784163aa6c05f396b1dea6\",\"name\": \"admin\",\"password_expires_at\": null},\"audit_ids\": [\"ek3mX-JhQJenPRxFAigh_g\",\"CY3Ti-L-Tcq4yx-8FXLA2w\"],\"expires_at\": \"2025-10-16T15:23:59.000000Z\",\"issued_at\": \"2025-10-15T15:24:35.000000Z\",\"roles\": [{\"id\": \"4bc86dbbfd224296a52cb5ba55bd9860\",\"name\": \"admin\"}],\"system\": {\"all\": true},\"catalog\": [{\"endpoints\": [{\"id\": \"11b3d6ec6cfc4ccc84f17fc8c1c3d102\",\"interface\": \"internal\",\"region_id\": \"RegionOne\",\"url\": \"http://172.32.0.248:9998\",\"region\": \"RegionOne\"}],\"id\": \"3b4fdb400757490bae14e602f1e3b8d8\",\"type\": \"panel\",\"name\": \"skyline\"}]}}")
        // );

        // When
        authModule.invalidateSystemAdminToken(testSystemAdminScopedToken);

        // Then

    }

    @Test
    @DisplayName("시스템은 service 계층에서 시스템 관리자 토큰이 아닌 토큰을 입력 시 예외를 던질 수 있다.")
    void givenSystemAdminToken_whenNormalTokenRequestInvaliateDirectly_thenThrowsException() throws Exception {
        // Given
        String testGeneralToken = "system-nonadmin-scoped-token";
        when(keystoneAPIExternalPort.getTokenObject(testGeneralToken)).thenReturn(new KeystoneToken(
            KeystoneTokenType.SCOPED,
            List.of("audit-id-000", "audit-id-001"),
            LocalDateTime.now().plusDays(2),
            LocalDateTime.now(),
            "test-admin-id",
            "test-admin",
            testGeneralToken,
            false
        ));

        assertThrows(
            // Then
            InvalidParameterException.class,
            // When
            () -> authModule.invalidateSystemAdminToken(testGeneralToken)
        );
    }

    // ==== Login with Refresh Token Tests ====

    @Test
    @DisplayName("로그인 시 Keystone 패스워드 인증으로 Access Token(UserToken)을 생성하고 저장할 수 있다.")
    void givenKeystonePasswordLoginRequest_whenGenerateAccessToken_thenReturnUserToken() {
        // given
        KeystonePasswordLoginRequest request = new KeystonePasswordLoginRequest(
            "testuser",
            "testpassword",
            "Default"
        );
        String userId = "test-user-id";
        String keystoneToken = "keystone-unscoped-token";
        String expectedAccessToken = "access-token-12345";
        LocalDateTime keystoneExpiresAt = LocalDateTime.now().plusDays(1);

        KeystoneToken mockKeystoneToken = new KeystoneToken(
            KeystoneTokenType.UNSCOPED,
            List.of("audit1", "audit2"),
            keystoneExpiresAt,
            LocalDateTime.now(),
            userId,
            "testuser",
            keystoneToken,
            false
        );

        when(keystoneAPIExternalPort.getUnscopedToken(request)).thenReturn(mockKeystoneToken);
        when(userTokenRepositoryPort.findAllByUserIdAndIsActiveTrue(userId)).thenReturn(List.of());
        when(jwtUtils.generateToken(userId)).thenReturn(expectedAccessToken);

        UserTokenEntity savedEntity = mock(UserTokenEntity.class);
        when(savedEntity.getUserId()).thenReturn(userId);
        when(savedEntity.getJwtToken()).thenReturn(expectedAccessToken);
        when(savedEntity.getKeystoneUnscopedToken()).thenReturn(keystoneToken);
        when(savedEntity.getKeystoneExpiresAt()).thenReturn(keystoneExpiresAt);
        when(userTokenRepositoryPort.save(any(UserTokenEntity.class))).thenReturn(savedEntity);

        // when
        UserToken userToken = authModule.generateAccessToken(request);

        // then
        assertNotNull(userToken);
        assertEquals(userId, userToken.getUserId());
        assertEquals(expectedAccessToken, userToken.getJwtToken());
        assertEquals(keystoneToken, userToken.getKeystoneUnscopedToken());
        verify(keystoneAPIExternalPort).getUnscopedToken(request);
        verify(userTokenRepositoryPort).deactivateAllByUserId(userId);
        verify(jwtUtils).generateToken(userId);
        verify(userTokenRepositoryPort).save(any(UserTokenEntity.class));
    }

    @Test
    @DisplayName("로그인 시 사용자 ID로 Refresh Token을 생성하고 저장할 수 있다.")
    void givenUserIdAndRequest_whenGenerateRefreshToken_thenReturnRefreshToken() {
        // given
        KeystonePasswordLoginRequest request = new KeystonePasswordLoginRequest(
            "testuser",
            "testpassword",
            "Default"
        );
        String userId = "test-user-id";
        String expectedRefreshToken = "refresh-token-12345";

        when(jwtUtils.generateRefreshToken(userId)).thenReturn(expectedRefreshToken);

        RefreshTokenEntity savedEntity = mock(RefreshTokenEntity.class);
        when(savedEntity.getUserId()).thenReturn(userId);
        when(savedEntity.getRefreshToken()).thenReturn(expectedRefreshToken);
        when(savedEntity.getExpiresAt()).thenReturn(LocalDateTime.now().plusDays(7));
        when(savedEntity.getIsActive()).thenReturn(true);
        when(refreshTokenRepositoryPort.save(any(RefreshTokenEntity.class))).thenReturn(savedEntity);

        // when
        RefreshToken refreshToken = authModule.generateRefreshToken(request, userId);

        // then
        assertNotNull(refreshToken);
        assertEquals(userId, refreshToken.getUserId());
        assertEquals(expectedRefreshToken, refreshToken.getRefreshToken());
        assertTrue(refreshToken.getIsActive());
        verify(jwtUtils).generateRefreshToken(userId);
        verify(refreshTokenRepositoryPort).save(any(com.acc.local.entity.RefreshTokenEntity.class));
    }

    // ==== Signup Tests ====

    @Test
    @DisplayName("회원가입 시 Keystone에 사용자를 생성하고 ACC DB에 사용자 정보를 저장할 수 있다.")
    void givenSignupRequest_whenSignup_thenCreateKeystoneUserAndSaveToDatabase() throws Exception {
        // given
       SignupRequest signupRequest = new SignupRequest(
            "hong123",
            "hong@example.com",
            "securePassword123!",
            "컴퓨터공학과",
            "2024123456",
            "010-1234-5678",
           AuthType.GOOGLE
        );
        String adminToken = "system-admin-token";
        String createdUserId = "created-user-id-12345";

        // Keystone 사용자 생성 응답 mock
        JsonNode userBody = objectMapper.readTree(
            "{\"user\": {\"id\": \"" + createdUserId + "\", \"name\": \"hong@example.com\", \"email\": \"hong@example.com\", \"enabled\": true, \"domain_id\": \"default\"}}"
        );
        ResponseEntity<JsonNode> mockKeystoneResponse = new ResponseEntity<>(userBody, HttpStatus.OK);
        when(keystoneAPIExternalPort.createUser(eq(adminToken), any())).thenReturn(mockKeystoneResponse);

        // Repository save mock
       UserDetailEntity savedUserDetail = UserDetailEntity.builder()
                .userId(createdUserId)
                .userName("hong123")
                .userPhoneNumber("010-1234-5678")
                .isAdmin(false)
                .build();
        when(userRepositoryPort.saveUserDetail(any(UserDetailEntity.class)))
                .thenReturn(savedUserDetail);

        UserAuthDetailEntity savedUserAuthDetail = UserAuthDetailEntity.builder()
                .userId(createdUserId)
                // .user(savedUserDetail)
                .department("컴퓨터공학과")
                .studentId("2024123456")
                .authType(0) // GOOGLE
                .userEmail("hong@example.com")
                .build();
        when(userRepositoryPort.saveUserAuth(any(UserAuthDetailEntity.class)))
                .thenReturn(savedUserAuthDetail);

        // when
        String resultUserId = authModule.signup(signupRequest, adminToken);

        // then
        assertEquals(createdUserId, resultUserId);
        verify(keystoneAPIExternalPort).createUser(eq(adminToken), any());
        verify(userRepositoryPort).saveUserDetail(any(UserDetailEntity.class));
        verify(userRepositoryPort).saveUserAuth(any(UserAuthDetailEntity.class));
        verify(keystoneAPIExternalPort).revokeToken(adminToken);
    }
}
