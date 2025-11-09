package com.acc.local.external.adapters.keystone;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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
		Map<String, Object> userRequest = Map.of("user", Map.of("name", "testUser"));
		JsonNode responseBody = objectMapper.readTree("{\"user\": {\"id\": \"user-id\"}}");
		ResponseEntity<JsonNode> expectedResponse = new ResponseEntity<>(responseBody, HttpStatus.CREATED);

		when(keystoneUserAPIModule.createUser(token, userRequest)).thenReturn(expectedResponse);

		// when
		ResponseEntity<JsonNode> result = keystoneAPIExternalAdapter.createUser(token, userRequest);

		// then
		assertNotNull(result);
		assertEquals(HttpStatus.CREATED, result.getStatusCode());
		verify(keystoneUserAPIModule).createUser(token, userRequest);
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
		ResponseEntity<JsonNode> result = keystoneAPIExternalAdapter.getUserDetail(userId, token);

		// then
		assertNotNull(result);
		assertEquals(HttpStatus.OK, result.getStatusCode());
		verify(keystoneUserAPIModule).getUserDetail(userId, token);
	}

	@Test
	@DisplayName("토큰과 사용자 ID, 사용자 요청으로 사용자를 업데이트할 수 있다")
	void givenUserIdAndTokenAndUserRequest_whenUpdateUser_thenReturnResponse() throws JsonProcessingException {
		// given
		String userId = "test-user-id";
		String token = "test-token";
		Map<String, Object> userRequest = Map.of("user", Map.of("name", "updatedUser"));
		JsonNode responseBody = objectMapper.readTree("{\"user\": {\"id\": \"" + userId + "\"}}");
		ResponseEntity<JsonNode> expectedResponse = new ResponseEntity<>(responseBody, HttpStatus.OK);

		when(keystoneUserAPIModule.updateUser(userId, token, userRequest)).thenReturn(expectedResponse);

		// when
		ResponseEntity<JsonNode> result = keystoneAPIExternalAdapter.updateUser(userId, token, userRequest);

		// then
		assertNotNull(result);
		assertEquals(HttpStatus.OK, result.getStatusCode());
		verify(keystoneUserAPIModule).updateUser(userId, token, userRequest);
	}

	@Test
	@DisplayName("토큰과 사용자 ID로 사용자를 삭제할 수 있다")
	void givenUserIdAndToken_whenDeleteUser_thenReturnResponse() throws JsonProcessingException {
		// given
		String userId = "test-user-id";
		String token = "test-token";
		JsonNode responseBody = objectMapper.readTree("{}");
		ResponseEntity<JsonNode> expectedResponse = new ResponseEntity<>(responseBody, HttpStatus.NO_CONTENT);

		when(keystoneUserAPIModule.deleteUser(userId, token)).thenReturn(expectedResponse);

		// when
		ResponseEntity<JsonNode> result = keystoneAPIExternalAdapter.deleteUser(userId, token);

		// then
		assertNotNull(result);
		verify(keystoneUserAPIModule).deleteUser(userId, token);
	}

	// ===== Project Methods =====

	@Test
	@DisplayName("토큰과 프로젝트 요청으로 프로젝트를 생성할 수 있다")
	void givenTokenAndProjectRequest_whenCreateProject_thenReturnResponse() throws JsonProcessingException {
		// given
		String token = "test-token";
		Map<String, Object> projectRequest = Map.of("project", Map.of("name", "testProject"));
		JsonNode responseBody = objectMapper.readTree("{\"project\": {\"id\": \"project-id\"}}");
		ResponseEntity<JsonNode> expectedResponse = new ResponseEntity<>(responseBody, HttpStatus.CREATED);

		when(keystoneProjectAPIModule.createProject(token, projectRequest)).thenReturn(expectedResponse);

		// when
		ResponseEntity<JsonNode> result = keystoneAPIExternalAdapter.createProject(token, projectRequest);

		// then
		assertNotNull(result);
		assertEquals(HttpStatus.CREATED, result.getStatusCode());
		verify(keystoneProjectAPIModule).createProject(token, projectRequest);
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
		ResponseEntity<JsonNode> result = keystoneAPIExternalAdapter.getProjectDetail(projectId, token);

		// then
		assertNotNull(result);
		assertEquals(HttpStatus.OK, result.getStatusCode());
		verify(keystoneProjectAPIModule).getProjectDetail(projectId, token);
	}

	@Test
	@DisplayName("토큰과 프로젝트 ID, 프로젝트 요청으로 프로젝트를 업데이트할 수 있다")
	void givenProjectIdAndTokenAndProjectRequest_whenUpdateProject_thenReturnResponse() throws JsonProcessingException {
		// given
		String projectId = "test-project-id";
		String token = "test-token";
		Map<String, Object> projectRequest = Map.of("project", Map.of("name", "updatedProject"));
		JsonNode responseBody = objectMapper.readTree("{\"project\": {\"id\": \"" + projectId + "\"}}");
		ResponseEntity<JsonNode> expectedResponse = new ResponseEntity<>(responseBody, HttpStatus.OK);

		when(keystoneProjectAPIModule.updateProject(projectId, token, projectRequest)).thenReturn(expectedResponse);

		// when
		ResponseEntity<JsonNode> result = keystoneAPIExternalAdapter.updateProject(projectId, token, projectRequest);

		// then
		assertNotNull(result);
		assertEquals(HttpStatus.OK, result.getStatusCode());
		verify(keystoneProjectAPIModule).updateProject(projectId, token, projectRequest);
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
		ResponseEntity<JsonNode> result = keystoneAPIExternalAdapter.deleteProject(projectId, token);

		// then
		assertNotNull(result);
		verify(keystoneProjectAPIModule).deleteProject(projectId, token);
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
		ResponseEntity<JsonNode> result = keystoneAPIExternalAdapter.getAccountPermissionList(userId, token);

		// then
		assertNotNull(result);
		assertEquals(HttpStatus.OK, result.getStatusCode());
		verify(keystoneRoleAPIModule).getAccountPermissionList(userId, token);
	}
}
