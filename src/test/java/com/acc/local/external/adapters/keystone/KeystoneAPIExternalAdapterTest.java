package com.acc.local.external.adapters.keystone;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.Map;

import com.acc.global.common.PageRequest;
import com.acc.local.domain.enums.project.ProjectRole;
import com.acc.local.dto.auth.UserKeystoneDto;
import com.acc.local.dto.project.ProjectListDto;
import com.acc.local.external.dto.keystone.CreateKeystoneProjectRequest;
import com.acc.local.external.dto.keystone.CreateKeystoneUserRequest;
import com.acc.local.external.dto.keystone.KeystoneProject;
import com.acc.local.external.dto.keystone.UpdateKeystoneProjectRequest;
import com.acc.local.external.dto.keystone.UpdateKeystoneUserRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.acc.local.domain.enums.auth.KeystoneTokenType;
import com.acc.local.dto.auth.KeystonePasswordLoginRequest;
import com.acc.local.dto.auth.KeystoneToken;
import com.acc.local.external.modules.keystone.KeystoneAuthAPIModule;
import com.acc.local.external.modules.keystone.KeystoneProjectAPIModule;
import com.acc.local.external.modules.keystone.KeystoneRoleAPIModule;
import com.acc.local.external.modules.keystone.KeystoneUserAPIModule;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@ExtendWith(MockitoExtension.class)
class KeystoneAPIExternalAdapterTest {

	@Mock
	private KeystoneAuthAPIModule keystoneAuthAPIModule;

	@Mock
	private KeystoneProjectAPIModule keystoneProjectAPIModule;

	@Mock
	private KeystoneUserAPIModule keystoneUserAPIModule;

	@Mock
	private KeystoneRoleAPIModule keystoneRoleAPIModule;

	@InjectMocks
	private KeystoneAPIExternalAdapter keystoneAPIExternalAdapter;

	private final ObjectMapper objectMapper = new ObjectMapper();

	// ===== Auth Methods =====

	@Test
	@DisplayName("패스워드 로그인 요청으로 언스코프 토큰을 받을 수 있다")
	void givenLoginRequest_whenGetUnscopedToken_thenReturnKeystoneToken() throws JsonProcessingException {
		// given
		KeystonePasswordLoginRequest loginRequest = new KeystonePasswordLoginRequest("testUser", "testPassword", "Default");
		String tokenValue = "test-unscoped-token";

		HttpHeaders headers = new HttpHeaders();
		headers.set("X-Subject-Token", tokenValue);
		JsonNode responseBody = objectMapper.readTree(
			"{\"token\": {\"methods\": [\"password\"], \"user\": {\"id\": \"user-id\", \"name\": \"testUser\"}, " +
			"\"audit_ids\": [\"audit-1\"], \"expires_at\": \"2025-12-31T23:59:59\", \"issued_at\": \"2025-01-01T00:00:00\"}}"
		);
		ResponseEntity<JsonNode> expectedResponse = new ResponseEntity<>(responseBody, headers, HttpStatus.OK);

		when(keystoneAuthAPIModule.issueUnscopedToken(any())).thenReturn(expectedResponse);

		// when
		KeystoneToken result = keystoneAPIExternalAdapter.getUnscopedToken(loginRequest);

		// then
		assertNotNull(result);
		assertEquals(tokenValue, result.token());
		assertEquals(KeystoneTokenType.UNSCOPED, result.tokenType());
		verify(keystoneAuthAPIModule).issueUnscopedToken(any());
	}

	@Test
	@DisplayName("프로젝트 ID와 언스코프 토큰으로 스코프 토큰을 받을 수 있다")
	void givenProjectIdAndUnscopedToken_whenGetScopedToken_thenReturnScopedToken() throws JsonProcessingException {
		// given
		String projectId = "test-project-id";
		String unscopedToken = "test-unscoped-token";
		String scopedToken = "test-scoped-token";

		HttpHeaders headers = new HttpHeaders();
		headers.set("X-Subject-Token", scopedToken);
		JsonNode responseBody = objectMapper.readTree(
			"{\"token\": {\"methods\": [\"token\"], \"user\": {\"id\": \"user-id\", \"name\": \"testUser\"}, " +
			"\"project\": {\"id\": \"" + projectId + "\", \"name\": \"testProject\"}, " +
			"\"audit_ids\": [\"audit-1\"], \"expires_at\": \"2025-12-31T23:59:59\", \"issued_at\": \"2025-01-01T00:00:00\"}}"
		);
		ResponseEntity<JsonNode> expectedResponse = new ResponseEntity<>(responseBody, headers, HttpStatus.OK);

		when(keystoneAuthAPIModule.issueScopedToken(any())).thenReturn(expectedResponse);

		// when
		KeystoneToken result = keystoneAPIExternalAdapter.getScopedToken(projectId, unscopedToken);

		// then
		assertNotNull(result);
		assertEquals(scopedToken, result.token());
		assertEquals(KeystoneTokenType.SCOPED, result.tokenType());
		verify(keystoneAuthAPIModule).issueScopedToken(any());
	}


	@Test
	@DisplayName("관리자 로그인 정보로 시스템 관리자 토큰을 받을 수 있다")
	void givenLoginRequest_whenGetAdminToken_thenReturnSystemAdminToken() throws JsonProcessingException {
		// given
		KeystonePasswordLoginRequest loginRequest = new KeystonePasswordLoginRequest("admin", "adminPassword", "Default");
		String unscopedToken = "test-unscoped-token";
		String systemAdminToken = "test-system-admin-token";

		// Unscoped token response
		HttpHeaders unscopedHeaders = new HttpHeaders();
		unscopedHeaders.set("X-Subject-Token", unscopedToken);
		JsonNode unscopedBody = objectMapper.readTree(
			"{\"token\": {\"methods\": [\"password\"], \"user\": {\"id\": \"admin-id\", \"name\": \"admin\"}, " +
			"\"audit_ids\": [\"audit-1\"], \"expires_at\": \"2025-12-31T23:59:59\", \"issued_at\": \"2025-01-01T00:00:00\"}}"
		);
		ResponseEntity<JsonNode> unscopedResponse = new ResponseEntity<>(unscopedBody, unscopedHeaders, HttpStatus.OK);

		// System admin token response
		HttpHeaders systemHeaders = new HttpHeaders();
		systemHeaders.set("X-Subject-Token", systemAdminToken);
		JsonNode systemBody = objectMapper.readTree(
			"{\"token\": {\"methods\": [\"token\"], \"user\": {\"id\": \"admin-id\", \"name\": \"admin\"}, " +
			"\"system\": {\"all\": \"true\"}, " +
			"\"audit_ids\": [\"audit-1\", \"audit-2\"], \"expires_at\": \"2025-12-31T23:59:59\", \"issued_at\": \"2025-01-01T00:00:00\"}}"
		);
		ResponseEntity<JsonNode> systemResponse = new ResponseEntity<>(systemBody, systemHeaders, HttpStatus.OK);

		when(keystoneAuthAPIModule.issueUnscopedToken(any())).thenReturn(unscopedResponse);
		when(keystoneAuthAPIModule.issueScopedToken(any())).thenReturn(systemResponse);

		// when
		KeystoneToken result = keystoneAPIExternalAdapter.getAdminToken(loginRequest);

		// then
		assertNotNull(result);
		assertEquals(systemAdminToken, result.token());
		assertTrue(result.isAdmin());
		verify(keystoneAuthAPIModule).issueUnscopedToken(any());
		verify(keystoneAuthAPIModule).issueScopedToken(any());
	}

	@Test
	@DisplayName("토큰을 폐기할 수 있다")
	void givenToken_whenRevokeToken_thenTokenIsRevoked() throws JsonProcessingException {
		// given
		String token = "test-token";
		JsonNode responseBody = objectMapper.readTree("{}");
		ResponseEntity<JsonNode> expectedResponse = new ResponseEntity<>(responseBody, HttpStatus.NO_CONTENT);

		when(keystoneAuthAPIModule.revokeToken(token)).thenReturn(expectedResponse);

		// when & then
		assertDoesNotThrow(() -> {
			keystoneAPIExternalAdapter.revokeToken(token);
		});

		verify(keystoneAuthAPIModule).revokeToken(token);
	}


	@Test
	@DisplayName("토큰으로 토큰 정보 객체를 조회할 수 있다")
	void givenToken_whenGetTokenObject_thenReturnKeystoneToken() throws JsonProcessingException {
		// given
		String token = "test-token";

		HttpHeaders headers = new HttpHeaders();
		headers.set("X-Subject-Token", token);
		JsonNode responseBody = objectMapper.readTree(
			"{\"token\": {\"methods\": [\"password\"], \"user\": {\"id\": \"user-id\", \"name\": \"testUser\"}, " +
			"\"audit_ids\": [\"audit-1\"], \"expires_at\": \"2025-12-31T23:59:59\", \"issued_at\": \"2025-01-01T00:00:00\"}}"
		);
		ResponseEntity<JsonNode> expectedResponse = new ResponseEntity<>(responseBody, headers, HttpStatus.OK);

		when(keystoneAuthAPIModule.getTokenInfo(token)).thenReturn(expectedResponse);

		// when
		KeystoneToken result = keystoneAPIExternalAdapter.getTokenObject(token);

		// then
		assertNotNull(result);
		assertEquals(token, result.token());
		verify(keystoneAuthAPIModule).getTokenInfo(token);
	}


	@Test
	@DisplayName("Keycloak 코드로 페더레이션 로그인을 요청할 수 있다")
	void givenKeycloakCode_whenRequestFederateLogin_thenReturnResponse() throws JsonProcessingException {
		// given
		String keycloakCode = "test-keycloak-code";
		JsonNode responseBody = objectMapper.readTree("{\"token\": {}}");
		ResponseEntity<JsonNode> expectedResponse = new ResponseEntity<>(responseBody, HttpStatus.OK);

		when(keystoneAuthAPIModule.requestFederateLogin(keycloakCode)).thenReturn(expectedResponse);

		// when
		ResponseEntity<JsonNode> result = keystoneAPIExternalAdapter.requestFederateLogin(keycloakCode);

		// then
		assertNotNull(result);
		assertEquals(HttpStatus.OK, result.getStatusCode());
		verify(keystoneAuthAPIModule).requestFederateLogin(keycloakCode);
	}

	// ===== User Methods =====

	@Test
	@DisplayName("토큰과 사용자 요청으로 사용자를 생성할 수 있다")
	void givenTokenAndUserRequest_whenCreateUser_thenReturnResponse() throws JsonProcessingException {
		// given
		String token = "test-token";
		CreateKeystoneUserRequest userRequest = CreateKeystoneUserRequest.builder()
			.email("testUser@example.com")
			.password("password")
			.isEnable(true)
			.build();
		JsonNode responseBody = objectMapper.readTree("{\"user\": {\"id\": \"user-id\", \"name\": \"testUser\"}}");
		ResponseEntity<JsonNode> expectedResponse = new ResponseEntity<>(responseBody, HttpStatus.CREATED);

		when(keystoneUserAPIModule.createUser(token, userRequest.toKeystoneRequest())).thenReturn(expectedResponse);

		// when
		UserKeystoneDto result = keystoneAPIExternalAdapter.createUser(token, userRequest);

		// then
		assertNotNull(result);
		assertEquals("user-id", result.id());
		verify(keystoneUserAPIModule).createUser(token, userRequest.toKeystoneRequest());
	}

	@Test
	@DisplayName("토큰과 사용자 ID로 사용자 상세 정보를 조회할 수 있다")
	void givenUserIdAndToken_whenGetUserDetail_thenReturnResponse() throws JsonProcessingException {
		// given
		String userId = "test-user-id";
		String token = "test-token";
		JsonNode responseBody = objectMapper.readTree("{\"user\": {\"id\": \"" + userId + "\"}}");
		ResponseEntity<JsonNode> expectedResponse = new ResponseEntity<>(responseBody, HttpStatus.OK);

		when(keystoneUserAPIModule.getUserDetail(userId, token)).thenReturn(expectedResponse);

		// when
		UserKeystoneDto result = keystoneAPIExternalAdapter.getUserDetail(userId, token);

		// then
		assertNotNull(result);
		assertEquals(userId, result.id());
		verify(keystoneUserAPIModule).getUserDetail(userId, token);
	}

	@Test
	@DisplayName("토큰과 사용자 ID, 사용자 요청으로 사용자를 업데이트할 수 있다")
	void givenUserIdAndTokenAndUserRequest_whenUpdateUser_thenReturnResponse() throws JsonProcessingException {
		// given
		String userId = "test-user-id";
		String token = "test-token";
		UpdateKeystoneUserRequest userRequest = UpdateKeystoneUserRequest.builder()
			.email("updatedUser@example.com")
			.isEnable(true)
			.build();
		JsonNode responseBody = objectMapper.readTree("{\"user\": {\"id\": \"" + userId + "\", \"name\": \"updatedUser\"}}");
		ResponseEntity<JsonNode> expectedResponse = new ResponseEntity<>(responseBody, HttpStatus.OK);

		when(keystoneUserAPIModule.updateUser(userId, token, userRequest.toKeystoneRequest())).thenReturn(expectedResponse);

		// when
		UserKeystoneDto result = keystoneAPIExternalAdapter.updateUser(userId, token, userRequest);

		// then
		assertNotNull(result);
		assertEquals(userId, result.id());
		verify(keystoneUserAPIModule).updateUser(userId, token, userRequest.toKeystoneRequest());
	}

	@Test
	@DisplayName("토큰과 사용자 ID로 사용자를 삭제할 수 있다")
	void givenUserIdAndToken_whenDeleteUser_thenReturnResponse() throws JsonProcessingException {
		// given
		String userId = "test-user-id";
		String token = "test-token";
		// when
		keystoneAPIExternalAdapter.deleteUser(userId, token);

		// then
		verify(keystoneUserAPIModule).deleteUser(userId, token);
	}

	// ===== Project Methods =====

	@Test
	@DisplayName("토큰과 프로젝트 요청으로 프로젝트를 생성할 수 있다")
	void givenTokenAndProjectRequest_whenCreateProject_thenReturnResponse() throws JsonProcessingException {
		// given
		String token = "test-token";
		CreateKeystoneProjectRequest projectRequest = CreateKeystoneProjectRequest.builder()
			.projectName("testProject")
			.projectDescription("description")
			.build();
		JsonNode responseBody = objectMapper.readTree("{\"project\": {\"id\": \"project-id\", \"name\": \"testProject\"}}");
		ResponseEntity<JsonNode> expectedResponse = new ResponseEntity<>(responseBody, HttpStatus.CREATED);

		when(keystoneProjectAPIModule.createProject(token, projectRequest.toKeystoneRequest())).thenReturn(expectedResponse);

		// when
		KeystoneProject result = keystoneAPIExternalAdapter.createProject(token, projectRequest);

		// then
		assertNotNull(result);
		verify(keystoneProjectAPIModule).createProject(token, projectRequest.toKeystoneRequest());
	}

	@Test
	@DisplayName("토큰과 프로젝트 ID로 프로젝트 상세 정보를 조회할 수 있다")
	void givenProjectIdAndToken_whenGetProjectDetail_thenReturnResponse() throws JsonProcessingException {
		// given
		String projectId = "test-project-id";
		String token = "test-token";
		JsonNode responseBody = objectMapper.readTree("{\"project\": {\"id\": \"" + projectId + "\"}}");
		ResponseEntity<JsonNode> expectedResponse = new ResponseEntity<>(responseBody, HttpStatus.OK);

		when(keystoneProjectAPIModule.getProjectDetail(projectId, token)).thenReturn(expectedResponse);

		// when
		KeystoneProject result = keystoneAPIExternalAdapter.getProjectDetail(projectId, token);

		// then
		assertNotNull(result);
		verify(keystoneProjectAPIModule).getProjectDetail(projectId, token);
	}

	@Test
	@DisplayName("토큰과 프로젝트 ID, 프로젝트 요청으로 프로젝트를 업데이트할 수 있다")
	void givenProjectIdAndTokenAndProjectRequest_whenUpdateProject_thenReturnResponse() throws JsonProcessingException {
		// given
		String projectId = "test-project-id";
		String token = "test-token";
		UpdateKeystoneProjectRequest projectRequest = UpdateKeystoneProjectRequest.builder()
			.name("updatedProject")
			.build();
		JsonNode responseBody = objectMapper.readTree("{\"project\": {\"id\": \"" + projectId + "\", \"name\": \"updatedProject\"}}");
		ResponseEntity<JsonNode> expectedResponse = new ResponseEntity<>(responseBody, HttpStatus.OK);

		when(keystoneProjectAPIModule.updateProject(projectId, token, projectRequest.toKeystoneRequest())).thenReturn(expectedResponse);

		// when
		KeystoneProject result = keystoneAPIExternalAdapter.updateProject(projectId, token, projectRequest);

		// then
		assertNotNull(result);
	//		assertEquals(HttpStatus.OK, result.getStatusCode());
		verify(keystoneProjectAPIModule).updateProject(projectId, token, projectRequest.toKeystoneRequest());
	}

	@Test
	@DisplayName("토큰과 프로젝트 ID로 프로젝트를 삭제할 수 있다")
	void givenProjectIdAndToken_whenDeleteProject_thenReturnResponse() throws JsonProcessingException {
		// given
		String projectId = "test-project-id";
		String token = "test-token";
		JsonNode responseBody = objectMapper.readTree("{}");
		ResponseEntity<JsonNode> expectedResponse = new ResponseEntity<>(responseBody, HttpStatus.NO_CONTENT);

		when(keystoneProjectAPIModule.deleteProject(projectId, token)).thenReturn(expectedResponse);

		// when
		keystoneAPIExternalAdapter.deleteProject(projectId, token);

		// then
//		assertNotNull(result);
		verify(keystoneProjectAPIModule).deleteProject(projectId, token);
	}

	@Test
	@DisplayName("프로젝트 목록 조회 시 PageRequest를 Keystone query로 변환하고 next marker만 파싱한다")
	void givenPageRequest_whenGetProjectsByProjectName_thenUseMarkerQueryAndParseLinks() throws JsonProcessingException {
		String token = "test-token";
		PageRequest pageRequest = new PageRequest();
		pageRequest.setLimit(1);
		JsonNode responseBody = objectMapper.readTree(
			"{\"projects\":[{\"id\":\"project-1\",\"name\":\"issue30\",\"description\":\"\"," +
				"\"is_domain\":false,\"enabled\":true,\"parent_id\":\"\"}]," +
				"\"links\":{\"self\":\"http://keystone/v3/projects?limit=1\"," +
				"\"next\":\"http://keystone/v3/projects?marker=project-1&limit=1\"," +
				"\"previous\":null}}"
		);
		ResponseEntity<JsonNode> response = new ResponseEntity<>(responseBody, HttpStatus.OK);
		when(keystoneProjectAPIModule.getProjects(eq(token), anyMap())).thenReturn(response);

		ProjectListDto result = keystoneAPIExternalAdapter.getProjectsByProjectName("issue30", pageRequest, token);

		assertEquals(1, result.projectList().size());
		assertTrue(result.pageInfo().isFirst());
		assertFalse(result.pageInfo().isLast());
		assertEquals("project-1", result.pageInfo().nextMarker());
		assertNull(result.pageInfo().prevMarker());
		@SuppressWarnings("unchecked")
		ArgumentCaptor<Map<String, String>> captor = ArgumentCaptor.forClass(Map.class);
		verify(keystoneProjectAPIModule).getProjects(eq(token), captor.capture());
		assertEquals("issue30", captor.getValue().get("name"));
		assertEquals("1", captor.getValue().get("limit"));
		assertFalse(captor.getValue().containsKey("marker"));
	}

	@Test
	@DisplayName("사용자 프로젝트 목록 조회 시 marker query map을 누락하지 않고 응답 첫 항목을 prevMarker로 반환한다")
	void givenMarkerPageRequest_whenGetUserProjectsByProjectName_thenPassMarkerQuery() throws JsonProcessingException {
		String token = "test-token";
		String userId = "user-id";
		PageRequest pageRequest = new PageRequest();
		pageRequest.setMarker("project-1");
		pageRequest.setLimit(1);
		JsonNode responseBody = objectMapper.readTree(
			"{\"projects\":[{\"id\":\"project-2\",\"name\":\"issue30\",\"description\":\"\"," +
				"\"is_domain\":false,\"enabled\":true,\"parent_id\":\"\"}]," +
				"\"links\":{\"self\":\"http://keystone/v3/users/user-id/projects?marker=project-1&limit=1\"," +
				"\"next\":null," +
				"\"previous\":\"http://keystone/v3/users/user-id/projects?marker=project-0&limit=1\"}}"
		);
		ResponseEntity<JsonNode> response = new ResponseEntity<>(responseBody, HttpStatus.OK);
		when(keystoneProjectAPIModule.getProjectsUser(eq(token), eq(userId), anyMap())).thenReturn(response);

		ProjectListDto result = keystoneAPIExternalAdapter.getUserProjectsByProjectName("issue30", pageRequest, userId, token);

		assertFalse(result.pageInfo().isFirst());
		assertTrue(result.pageInfo().isLast());
		assertNull(result.pageInfo().nextMarker());
		assertEquals("project-2", result.pageInfo().prevMarker());
		@SuppressWarnings("unchecked")
		ArgumentCaptor<Map<String, String>> captor = ArgumentCaptor.forClass(Map.class);
		verify(keystoneProjectAPIModule).getProjectsUser(eq(token), eq(userId), captor.capture());
		assertEquals("issue30", captor.getValue().get("name"));
		assertEquals("project-1", captor.getValue().get("marker"));
		assertEquals("1", captor.getValue().get("limit"));
	}

	@Test
	@DisplayName("사용자 프로젝트 전체 조회 시 기본 limit을 보내지 않고 모든 페이지를 수집한다")
	void givenNullPageRequest_whenGetUserProjectsByProjectName_thenFetchAllProjectsWithoutLimit() throws JsonProcessingException {
		String token = "test-token";
		String userId = "user-id";
		JsonNode firstPageBody = objectMapper.readTree(
			"{\"projects\":[" +
				"{\"id\":\"project-1\",\"name\":\"issue30-1\",\"description\":\"\",\"is_domain\":false,\"enabled\":true,\"parent_id\":\"\"}," +
				"{\"id\":\"project-2\",\"name\":\"issue30-2\",\"description\":\"\",\"is_domain\":false,\"enabled\":true,\"parent_id\":\"\"}]," +
				"\"links\":{\"self\":\"http://keystone/v3/users/user-id/projects?name=issue30\"," +
				"\"next\":\"http://keystone/v3/users/user-id/projects?marker=project-2\"," +
				"\"previous\":null}}"
		);
		JsonNode secondPageBody = objectMapper.readTree(
			"{\"projects\":[" +
				"{\"id\":\"project-3\",\"name\":\"issue30-3\",\"description\":\"\",\"is_domain\":false,\"enabled\":true,\"parent_id\":\"\"}]," +
				"\"links\":{\"self\":\"http://keystone/v3/users/user-id/projects?marker=project-2\"," +
				"\"next\":null," +
				"\"previous\":null}}"
		);
		ResponseEntity<JsonNode> firstPageResponse = new ResponseEntity<>(firstPageBody, HttpStatus.OK);
		ResponseEntity<JsonNode> secondPageResponse = new ResponseEntity<>(secondPageBody, HttpStatus.OK);
		when(keystoneProjectAPIModule.getProjectsUser(eq(token), eq(userId), anyMap()))
			.thenReturn(firstPageResponse, secondPageResponse);

		ProjectListDto result = keystoneAPIExternalAdapter.getUserProjectsByProjectName("issue30", null, userId, token);

		assertEquals(3, result.projectList().size());
		assertEquals("project-1", result.projectList().get(0).getId());
		assertEquals("project-3", result.projectList().get(2).getId());
		assertTrue(result.pageInfo().isFirst());
		assertTrue(result.pageInfo().isLast());
		assertNull(result.pageInfo().nextMarker());
		assertNull(result.pageInfo().prevMarker());
		@SuppressWarnings("unchecked")
		ArgumentCaptor<Map<String, String>> captor = ArgumentCaptor.forClass(Map.class);
		verify(keystoneProjectAPIModule, times(2)).getProjectsUser(eq(token), eq(userId), captor.capture());
		assertEquals("issue30", captor.getAllValues().get(0).get("name"));
		assertFalse(captor.getAllValues().get(0).containsKey("limit"));
		assertFalse(captor.getAllValues().get(0).containsKey("marker"));
		assertEquals("issue30", captor.getAllValues().get(1).get("name"));
		assertEquals("project-2", captor.getAllValues().get(1).get("marker"));
		assertFalse(captor.getAllValues().get(1).containsKey("limit"));
	}

	@Test
	@DisplayName("관리자 프로젝트 목록은 direction=prev이면 현재 페이지 첫 marker 이전 페이지를 반환한다")
	void givenPrevDirection_whenGetProjectsByProjectName_thenReturnPreviousProjectPage() throws JsonProcessingException {
		String token = "test-token";
		PageRequest pageRequest = new PageRequest();
		pageRequest.setMarker("project-3");
		pageRequest.setLimit(2);
		pageRequest.setDirection(PageRequest.Direction.prev);
		JsonNode firstPageBody = objectMapper.readTree(
			"{\"projects\":[" +
				"{\"id\":\"project-1\",\"name\":\"issue30-1\",\"description\":\"\",\"is_domain\":false,\"enabled\":true,\"parent_id\":\"\"}," +
				"{\"id\":\"project-2\",\"name\":\"issue30-2\",\"description\":\"\",\"is_domain\":false,\"enabled\":true,\"parent_id\":\"\"}]," +
				"\"links\":{\"self\":\"http://keystone/v3/projects?limit=2\"," +
				"\"next\":\"http://keystone/v3/projects?marker=project-2&limit=2\"," +
				"\"previous\":null}}"
		);
		JsonNode currentPageBody = objectMapper.readTree(
			"{\"projects\":[" +
				"{\"id\":\"project-3\",\"name\":\"issue30-3\",\"description\":\"\",\"is_domain\":false,\"enabled\":true,\"parent_id\":\"\"}," +
				"{\"id\":\"project-4\",\"name\":\"issue30-4\",\"description\":\"\",\"is_domain\":false,\"enabled\":true,\"parent_id\":\"\"}]," +
				"\"links\":{\"self\":\"http://keystone/v3/projects?marker=project-2&limit=2\"," +
				"\"next\":\"http://keystone/v3/projects?marker=project-4&limit=2\"," +
				"\"previous\":null}}"
		);
		ResponseEntity<JsonNode> firstPageResponse = new ResponseEntity<>(firstPageBody, HttpStatus.OK);
		ResponseEntity<JsonNode> currentPageResponse = new ResponseEntity<>(currentPageBody, HttpStatus.OK);
		when(keystoneProjectAPIModule.getProjects(eq(token), anyMap()))
			.thenReturn(firstPageResponse, currentPageResponse);

		ProjectListDto result = keystoneAPIExternalAdapter.getProjectsByProjectName("issue30", pageRequest, token);

		assertEquals(2, result.projectList().size());
		assertEquals("project-1", result.projectList().get(0).getId());
		assertEquals("project-2", result.projectList().get(1).getId());
		assertTrue(result.pageInfo().isFirst());
		assertFalse(result.pageInfo().isLast());
		assertEquals("project-2", result.pageInfo().nextMarker());
		assertNull(result.pageInfo().prevMarker());
		@SuppressWarnings("unchecked")
		ArgumentCaptor<Map<String, String>> captor = ArgumentCaptor.forClass(Map.class);
		verify(keystoneProjectAPIModule, times(2)).getProjects(eq(token), captor.capture());
		assertEquals("issue30", captor.getAllValues().get(0).get("name"));
		assertEquals("2", captor.getAllValues().get(0).get("limit"));
		assertFalse(captor.getAllValues().get(0).containsKey("marker"));
		assertEquals("project-2", captor.getAllValues().get(1).get("marker"));
	}

	// ===== Role Methods =====

	@Test
	@DisplayName("토큰과 사용자 ID로 계정 권한 목록을 조회할 수 있다")
	void givenUserIdAndToken_whenGetAccountPermissionList_thenReturnResponse() throws JsonProcessingException {
		// given
		String userId = "test-user-id";
		String token = "test-token";
		JsonNode responseBody = objectMapper.readTree("{\"role_assignments\": []}");
		ResponseEntity<JsonNode> expectedResponse = new ResponseEntity<>(responseBody, HttpStatus.OK);

		when(keystoneRoleAPIModule.getAccountPermissionList(userId, token)).thenReturn(expectedResponse);

		// when
		Map<String, ProjectRole> result = keystoneAPIExternalAdapter.getAccountPermissionList(userId, token);

		// then
		assertNotNull(result);
//		assertEquals(HttpStatus.OK, result.getStatusCode());
		verify(keystoneRoleAPIModule).getAccountPermissionList(userId, token);
	}
}
