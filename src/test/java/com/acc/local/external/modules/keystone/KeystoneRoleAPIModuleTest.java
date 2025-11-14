package com.acc.local.external.modules.keystone;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.Collections;

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
class KeystoneRoleAPIModuleTest {

	@Mock
	private OpenstackAPICallModule openstackAPICallModule;

	@InjectMocks
	private KeystoneRoleAPIModule keystoneRoleAPIModule;

	private final ObjectMapper objectMapper = new ObjectMapper();

	@Test
	@DisplayName("토큰과 사용자 ID로 계정 권한 목록을 조회할 수 있다")
	void givenUserIdAndToken_whenGetAccountPermissionList_thenReturnPermissionList() throws Exception {
		// given
		String userId = "test-user-id";
		String token = "test-token";
		String expectedUri = KeystoneRoutes.GET_ASSIGNED_PERMISSIONS.replace("{user_id}", userId)
			+ "?user.id=" + userId + "&effective&include_names=true";
		JsonNode responseBody = objectMapper.readTree(
			"{\"role_assignments\": [{\"scope\": {\"project\": {\"id\": \"project-id\"}}, \"role\": {\"name\": \"member\"}}]}"
		);
		ResponseEntity<JsonNode> expectedResponse = new ResponseEntity<>(responseBody, HttpStatus.OK);

		when(openstackAPICallModule.callGetAPI(
			eq(expectedUri),
			eq(Collections.singletonMap("X-Auth-Token", token)),
			eq(Collections.emptyMap()),
			eq(5000)
		)).thenReturn(expectedResponse);

		// when
		ResponseEntity<JsonNode> actualResponse = keystoneRoleAPIModule.getAccountPermissionList(userId, token);

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
	@DisplayName("권한 목록 조회 시 역할 할당이 없는 경우에도 정상 응답을 반환한다")
	void givenUserIdAndToken_whenGetAccountPermissionListWithNoRoles_thenReturnEmptyRoleAssignments() throws Exception {
		// given
		String userId = "test-user-id";
		String token = "test-token";
		String expectedUri = KeystoneRoutes.GET_ASSIGNED_PERMISSIONS.replace("{user_id}", userId)
			+ "?user.id=" + userId + "&effective&include_names=true";
		JsonNode responseBody = objectMapper.readTree("{\"role_assignments\": []}");
		ResponseEntity<JsonNode> expectedResponse = new ResponseEntity<>(responseBody, HttpStatus.OK);

		when(openstackAPICallModule.callGetAPI(
			eq(expectedUri),
			eq(Collections.singletonMap("X-Auth-Token", token)),
			eq(Collections.emptyMap()),
			eq(5000)
		)).thenReturn(expectedResponse);

		// when
		ResponseEntity<JsonNode> actualResponse = keystoneRoleAPIModule.getAccountPermissionList(userId, token);

		// then
		assertNotNull(actualResponse);
		assertEquals(HttpStatus.OK, actualResponse.getStatusCode());
		assertTrue(actualResponse.getBody().get("role_assignments").isArray());
		assertEquals(0, actualResponse.getBody().get("role_assignments").size());
		verify(openstackAPICallModule).callGetAPI(
			eq(expectedUri),
			anyMap(),
			eq(Collections.emptyMap()),
			eq(5000)
		);
	}
}
