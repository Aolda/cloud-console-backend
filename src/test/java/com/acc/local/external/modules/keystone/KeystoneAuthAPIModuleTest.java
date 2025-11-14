package com.acc.local.external.modules.keystone;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.Collections;
import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.acc.local.external.modules.OpenstackAPICallModule;
import com.acc.local.external.modules.keystone.constant.KeystoneRoutes;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@ExtendWith(MockitoExtension.class)
class KeystoneAuthAPIModuleTest {

	@Mock
	private OpenstackAPICallModule openstackAPICallModule;

	@InjectMocks
	private KeystoneAuthAPIModule keystoneAuthAPIModule;

	private final ObjectMapper objectMapper = new ObjectMapper();

	@Test
	@DisplayName("Keycloak 코드로 페더레이션 로그인을 요청할 수 있다")
	void givenKeycloakCode_whenRequestFederateLogin_thenReturnTokenResponse() throws Exception {
		// given
		String keycloakCode = "test-keycloak-code";
		JsonNode responseBody = objectMapper.readTree("{\"token\": {\"expires_at\": \"2025-12-31T23:59:59Z\"}}");
		ResponseEntity<JsonNode> expectedResponse = new ResponseEntity<>(responseBody, HttpStatus.OK);

		when(openstackAPICallModule.callPostAPI(
			eq(KeystoneRoutes.TOKEN_AUTH_FEDERATE),
			eq(Collections.singletonMap("Authorization", "Bearer " + keycloakCode)),
			eq(Collections.emptyMap()),
			eq(5000)
		)).thenReturn(expectedResponse);

		// when
		ResponseEntity<JsonNode> actualResponse = keystoneAuthAPIModule.requestFederateLogin(keycloakCode);

		// then
		assertNotNull(actualResponse);
		assertEquals(HttpStatus.OK, actualResponse.getStatusCode());
		assertEquals(responseBody, actualResponse.getBody());
		verify(openstackAPICallModule).callPostAPI(
			eq(KeystoneRoutes.TOKEN_AUTH_FEDERATE),
			anyMap(),
			eq(Collections.emptyMap()),
			eq(5000)
		);
	}

	@Test
	@DisplayName("토큰으로 토큰 정보를 조회할 수 있다")
	void givenToken_whenGetTokenInfo_thenReturnTokenInfo() throws Exception {
		// given
		String token = "test-token";
		JsonNode responseBody = objectMapper.readTree("{\"token\": {\"user\": {\"id\": \"user-id\"}}}");
		ResponseEntity<JsonNode> expectedResponse = new ResponseEntity<>(responseBody, HttpStatus.OK);

		when(openstackAPICallModule.callGetAPI(
			eq(KeystoneRoutes.TOKEN_AUTH_DEFAULT),
			anyMap(),
			eq(Collections.emptyMap()),
			eq(5000)
		)).thenReturn(expectedResponse);

		// when
		ResponseEntity<JsonNode> actualResponse = keystoneAuthAPIModule.getTokenInfo(token);

		// then
		assertNotNull(actualResponse);
		assertEquals(HttpStatus.OK, actualResponse.getStatusCode());
		assertEquals(responseBody, actualResponse.getBody());
		verify(openstackAPICallModule).callGetAPI(
			eq(KeystoneRoutes.TOKEN_AUTH_DEFAULT),
			anyMap(),
			eq(Collections.emptyMap()),
			eq(5000)
		);
	}

	@Test
	@DisplayName("스코프 토큰 정보를 조회할 수 있다")
	void givenTokenAndRequest_whenGetScopeTokenInfo_thenReturnScopeTokenInfo() throws Exception {
		// given
		String token = "test-token";
		Map<String, Object> request = Collections.emptyMap();
		JsonNode responseBody = objectMapper.readTree("{\"token\": {\"project\": {\"id\": \"project-id\"}}}");
		ResponseEntity<JsonNode> expectedResponse = new ResponseEntity<>(responseBody, HttpStatus.OK);

		when(openstackAPICallModule.callGetAPI(
			eq(KeystoneRoutes.TOKEN_AUTH_DEFAULT),
			eq(Collections.singletonMap("X-Auth-Token", token)),
			eq(Collections.emptyMap()),
			eq(5000)
		)).thenReturn(expectedResponse);

		// when
		ResponseEntity<JsonNode> actualResponse = keystoneAuthAPIModule.getScopeTokenInfo(token, request);

		// then
		assertNotNull(actualResponse);
		assertEquals(HttpStatus.OK, actualResponse.getStatusCode());
		assertEquals(responseBody, actualResponse.getBody());
		verify(openstackAPICallModule).callGetAPI(
			eq(KeystoneRoutes.TOKEN_AUTH_DEFAULT),
			anyMap(),
			eq(Collections.emptyMap()),
			eq(5000)
		);
	}

	@Test
	@DisplayName("스코프 토큰을 발급할 수 있다")
	void givenTokenRequest_whenIssueScopedToken_thenReturnScopedToken() throws Exception {
		// given
		Map<String, Object> tokenRequest = Map.of("auth", Map.of("identity", Map.of("methods", "token")));
		JsonNode responseBody = objectMapper.readTree("{\"token\": {\"project\": {\"id\": \"project-id\"}}}");
		ResponseEntity<JsonNode> expectedResponse = new ResponseEntity<>(responseBody, HttpStatus.OK);

		when(openstackAPICallModule.callPostAPI(
			eq(KeystoneRoutes.TOKEN_AUTH_DEFAULT),
			eq(Collections.emptyMap()),
			eq(tokenRequest),
			eq(5000)
		)).thenReturn(expectedResponse);

		// when
		ResponseEntity<JsonNode> actualResponse = keystoneAuthAPIModule.issueScopedToken(tokenRequest);

		// then
		assertNotNull(actualResponse);
		assertEquals(HttpStatus.OK, actualResponse.getStatusCode());
		assertEquals(responseBody, actualResponse.getBody());
		verify(openstackAPICallModule).callPostAPI(
			eq(KeystoneRoutes.TOKEN_AUTH_DEFAULT),
			eq(Collections.emptyMap()),
			eq(tokenRequest),
			eq(5000)
		);
	}

	@Test
	@DisplayName("언스코프 토큰을 발급할 수 있다")
	void givenPasswordAuthRequest_whenIssueUnscopedToken_thenReturnUnscopedToken() throws Exception {
		// given
		Map<String, Object> passwordAuthRequest = Map.of("auth", Map.of("identity", Map.of("methods", "password")));
		JsonNode responseBody = objectMapper.readTree("{\"token\": {\"user\": {\"id\": \"user-id\"}}}");
		ResponseEntity<JsonNode> expectedResponse = new ResponseEntity<>(responseBody, HttpStatus.OK);

		when(openstackAPICallModule.callPostAPI(
			eq(KeystoneRoutes.TOKEN_AUTH_DEFAULT),
			eq(Collections.emptyMap()),
			eq(passwordAuthRequest),
			eq(5000)
		)).thenReturn(expectedResponse);

		// when
		ResponseEntity<JsonNode> actualResponse = keystoneAuthAPIModule.issueUnscopedToken(passwordAuthRequest);

		// then
		assertNotNull(actualResponse);
		assertEquals(HttpStatus.OK, actualResponse.getStatusCode());
		assertEquals(responseBody, actualResponse.getBody());
		verify(openstackAPICallModule).callPostAPI(
			eq(KeystoneRoutes.TOKEN_AUTH_DEFAULT),
			eq(Collections.emptyMap()),
			eq(passwordAuthRequest),
			eq(5000)
		);
	}

	@Test
	@DisplayName("토큰을 폐기할 수 있다")
	void givenToken_whenRevokeToken_thenTokenIsRevoked() throws Exception {
		// given
		String token = "test-token";
		JsonNode responseBody = objectMapper.readTree("{}");
		ResponseEntity<JsonNode> expectedResponse = new ResponseEntity<>(responseBody, HttpStatus.NO_CONTENT);

		when(openstackAPICallModule.callDeleteAPI(
			eq(KeystoneRoutes.TOKEN_AUTH_DEFAULT),
			anyMap(),
			eq(5000)
		)).thenReturn(expectedResponse);

		// when
		ResponseEntity<JsonNode> actualResponse = keystoneAuthAPIModule.revokeToken(token);

		// then
		assertNotNull(actualResponse);
		verify(openstackAPICallModule).callDeleteAPI(
			eq(KeystoneRoutes.TOKEN_AUTH_DEFAULT),
			anyMap(),
			eq(5000)
		);
	}
}