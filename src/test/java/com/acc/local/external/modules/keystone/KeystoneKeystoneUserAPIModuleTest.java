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
class KeystoneKeystoneUserAPIModuleTest {

	@Mock
	private OpenstackAPICallModule openstackAPICallModule;

	@InjectMocks
	private KeystoneUserAPIModule keystoneUserAPIModule;

	private final ObjectMapper objectMapper = new ObjectMapper();

	@Test
	@DisplayName("토큰과 사용자 정보로 사용자를 생성할 수 있다")
	void givenTokenAndUserRequest_whenCreateUser_thenReturnCreatedUser() throws Exception {
		// given
		String token = "test-token";
		Map<String, Object> userRequest = Map.of("user", Map.of("name", "testUser", "email", "test@example.com"));
		JsonNode responseBody = objectMapper.readTree(
			"{\"user\": {\"id\": \"user-id\", \"name\": \"testUser\", \"email\": \"test@example.com\"}}"
		);
		ResponseEntity<JsonNode> expectedResponse = new ResponseEntity<>(responseBody, HttpStatus.CREATED);

		when(openstackAPICallModule.callPostAPI(
			eq(KeystoneRoutes.CREATE_USER),
			eq(Collections.singletonMap("X-Auth-Token", token)),
			eq(userRequest),
			eq(5000)
		)).thenReturn(expectedResponse);

		// when
		ResponseEntity<JsonNode> actualResponse = keystoneUserAPIModule.createUser(token, userRequest);

		// then
		assertNotNull(actualResponse);
		assertEquals(HttpStatus.CREATED, actualResponse.getStatusCode());
		assertEquals(responseBody, actualResponse.getBody());
		verify(openstackAPICallModule).callPostAPI(
			eq(KeystoneRoutes.CREATE_USER),
			anyMap(),
			eq(userRequest),
			eq(5000)
		);
	}

	@Test
	@DisplayName("토큰과 사용자 ID로 사용자 상세 정보를 조회할 수 있다")
	void givenUserIdAndToken_whenGetUserDetail_thenReturnUserDetail() throws Exception {
		// given
		String userId = "test-user-id";
		String token = "test-token";
		String expectedUri = KeystoneRoutes.POST_USER.replace("{user_id}", userId);
		JsonNode responseBody = objectMapper.readTree(
			"{\"user\": {\"id\": \"" + userId + "\", \"name\": \"testUser\", \"email\": \"test@example.com\"}}"
		);
		ResponseEntity<JsonNode> expectedResponse = new ResponseEntity<>(responseBody, HttpStatus.OK);

		when(openstackAPICallModule.callGetAPI(
			eq(expectedUri),
			eq(Collections.singletonMap("X-Auth-Token", token)),
			eq(Collections.emptyMap()),
			eq(5000)
		)).thenReturn(expectedResponse);

		// when
		ResponseEntity<JsonNode> actualResponse = keystoneUserAPIModule.getUserDetail(userId, token);

		// then
		assertNotNull(actualResponse);
		assertEquals(HttpStatus.OK, actualResponse.getStatusCode());
		assertEquals(responseBody, actualResponse.getBody());
		verify(openstackAPICallModule).callGetAPI(
			eq(expectedUri),
			anyMap(),
			eq(Collections.emptyMap()),
			eq(5000)
		);
	}

	@Test
	@DisplayName("토큰과 사용자 ID, 수정할 정보로 사용자를 업데이트할 수 있다")
	void givenUserIdAndTokenAndUserRequest_whenUpdateUser_thenReturnUpdatedUser() throws Exception {
		// given
		String userId = "test-user-id";
		String token = "test-token";
		String expectedUri = KeystoneRoutes.POST_USER.replace("{user_id}", userId);
		Map<String, Object> userRequest = Map.of("user", Map.of("name", "updatedUser"));
		JsonNode responseBody = objectMapper.readTree(
			"{\"user\": {\"id\": \"" + userId + "\", \"name\": \"updatedUser\"}}"
		);
		ResponseEntity<JsonNode> expectedResponse = new ResponseEntity<>(responseBody, HttpStatus.OK);

		when(openstackAPICallModule.callPutAPI(
			eq(expectedUri),
			eq(Collections.singletonMap("X-Auth-Token", token)),
			eq(userRequest),
			eq(5000)
		)).thenReturn(expectedResponse);

		// when
		ResponseEntity<JsonNode> actualResponse = keystoneUserAPIModule.updateUser(userId, token, userRequest);

		// then
		assertNotNull(actualResponse);
		assertEquals(HttpStatus.OK, actualResponse.getStatusCode());
		assertEquals(responseBody, actualResponse.getBody());
		verify(openstackAPICallModule).callPutAPI(
			eq(expectedUri),
			anyMap(),
			eq(userRequest),
			eq(5000)
		);
	}

	@Test
	@DisplayName("토큰과 사용자 ID로 사용자를 삭제할 수 있다")
	void givenUserIdAndToken_whenDeleteUser_thenUserIsDeleted() throws Exception {
		// given
		String userId = "test-user-id";
		String token = "test-token";
		String expectedUri = KeystoneRoutes.POST_USER.replace("{user_id}", userId);
		JsonNode responseBody = objectMapper.readTree("{}");
		ResponseEntity<JsonNode> expectedResponse = new ResponseEntity<>(responseBody, HttpStatus.NO_CONTENT);

		when(openstackAPICallModule.callDeleteAPI(
			eq(expectedUri),
			eq(Collections.singletonMap("X-Auth-Token", token)),
			eq(5000)
		)).thenReturn(expectedResponse);

		// when
		ResponseEntity<JsonNode> actualResponse = keystoneUserAPIModule.deleteUser(userId, token);

		// then
		assertNotNull(actualResponse);
		verify(openstackAPICallModule).callDeleteAPI(
			eq(expectedUri),
			anyMap(),
			eq(5000)
		);
	}
}
