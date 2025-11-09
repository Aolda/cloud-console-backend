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
class KeystoneProjectAPIModuleTest {

	@Mock
	private OpenstackAPICallModule openstackAPICallModule;

	@InjectMocks
	private KeystoneProjectAPIModule keystoneProjectAPIModule;

	private final ObjectMapper objectMapper = new ObjectMapper();

	@Test
	@DisplayName("토큰과 프로젝트 정보로 프로젝트를 생성할 수 있다")
	void givenTokenAndProjectRequest_whenCreateProject_thenReturnCreatedProject() throws Exception {
		// given
		String token = "test-token";
		Map<String, Object> projectRequest = Map.of("project", Map.of("name", "testProject", "enabled", true));
		JsonNode responseBody = objectMapper.readTree(
			"{\"project\": {\"id\": \"project-id\", \"name\": \"testProject\", \"enabled\": true}}"
		);
		ResponseEntity<JsonNode> expectedResponse = new ResponseEntity<>(responseBody, HttpStatus.CREATED);

		when(openstackAPICallModule.callPostAPI(
			eq(KeystoneRoutes.CREATE_PROJECT),
			eq(Collections.singletonMap("X-Auth-Token", token)),
			eq(projectRequest),
			eq(5000)
		)).thenReturn(expectedResponse);

		// when
		ResponseEntity<JsonNode> actualResponse = keystoneProjectAPIModule.createProject(token, projectRequest);

		// then
		assertNotNull(actualResponse);
		assertEquals(HttpStatus.CREATED, actualResponse.getStatusCode());
		assertEquals(responseBody, actualResponse.getBody());
		verify(openstackAPICallModule).callPostAPI(
			eq(KeystoneRoutes.CREATE_PROJECT),
			anyMap(),
			eq(projectRequest),
			eq(5000)
		);
	}

	@Test
	@DisplayName("토큰과 프로젝트 ID로 프로젝트 상세 정보를 조회할 수 있다")
	void givenProjectIdAndToken_whenGetProjectDetail_thenReturnProjectDetail() throws Exception {
		// given
		String projectId = "test-project-id";
		String token = "test-token";
		String expectedUri = KeystoneRoutes.POST_PROJECT.replace("{project_id}", projectId);
		JsonNode responseBody = objectMapper.readTree(
			"{\"project\": {\"id\": \"" + projectId + "\", \"name\": \"testProject\", \"enabled\": true}}"
		);
		ResponseEntity<JsonNode> expectedResponse = new ResponseEntity<>(responseBody, HttpStatus.OK);

		when(openstackAPICallModule.callGetAPI(
			eq(expectedUri),
			eq(Collections.singletonMap("X-Auth-Token", token)),
			eq(Collections.emptyMap()),
			eq(5000)
		)).thenReturn(expectedResponse);

		// when
		ResponseEntity<JsonNode> actualResponse = keystoneProjectAPIModule.getProjectDetail(projectId, token);

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
	@DisplayName("토큰과 프로젝트 ID, 수정할 정보로 프로젝트를 업데이트할 수 있다")
	void givenProjectIdAndTokenAndProjectRequest_whenUpdateProject_thenReturnUpdatedProject() throws Exception {
		// given
		String projectId = "test-project-id";
		String token = "test-token";
		String expectedUri = KeystoneRoutes.POST_PROJECT.replace("{project_id}", projectId);
		Map<String, Object> projectRequest = Map.of("project", Map.of("name", "updatedProject"));
		JsonNode responseBody = objectMapper.readTree(
			"{\"project\": {\"id\": \"" + projectId + "\", \"name\": \"updatedProject\"}}"
		);
		ResponseEntity<JsonNode> expectedResponse = new ResponseEntity<>(responseBody, HttpStatus.OK);

		when(openstackAPICallModule.callPatchAPI(
			eq(expectedUri),
			eq(Collections.singletonMap("X-Auth-Token", token)),
			eq(projectRequest),
			eq(5000)
		)).thenReturn(expectedResponse);

		// when
		ResponseEntity<JsonNode> actualResponse = keystoneProjectAPIModule.updateProject(projectId, token, projectRequest);

		// then
		assertNotNull(actualResponse);
		assertEquals(HttpStatus.OK, actualResponse.getStatusCode());
		assertEquals(responseBody, actualResponse.getBody());
		verify(openstackAPICallModule).callPatchAPI(
			eq(expectedUri),
			anyMap(),
			eq(projectRequest),
			eq(5000)
		);
	}

	@Test
	@DisplayName("토큰과 프로젝트 ID로 프로젝트를 삭제할 수 있다")
	void givenProjectIdAndToken_whenDeleteProject_thenProjectIsDeleted() throws Exception {
		// given
		String projectId = "test-project-id";
		String token = "test-token";
		String expectedUri = KeystoneRoutes.POST_PROJECT.replace("{project_id}", projectId);
		JsonNode responseBody = objectMapper.readTree("{}");
		ResponseEntity<JsonNode> expectedResponse = new ResponseEntity<>(responseBody, HttpStatus.NO_CONTENT);

		when(openstackAPICallModule.callDeleteAPI(
			eq(expectedUri),
			eq(Collections.singletonMap("X-Auth-Token", token)),
			eq(5000)
		)).thenReturn(expectedResponse);

		// when
		ResponseEntity<JsonNode> actualResponse = keystoneProjectAPIModule.deleteProject(projectId, token);

		// then
		assertNotNull(actualResponse);
		verify(openstackAPICallModule).callDeleteAPI(
			eq(expectedUri),
			anyMap(),
			eq(5000)
		);
	}
}
