package com.acc.local.service.modules.auth;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.acc.local.domain.model.KeystoneProject;
import com.acc.local.domain.model.User;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFunction;
import org.springframework.web.reactive.function.client.WebClient;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import reactor.core.publisher.Mono;

@ExtendWith(MockitoExtension.class)
class KeystoneModuleTest {

	private static final Logger log = LoggerFactory.getLogger(KeystoneModuleTest.class);
	@Mock
	private WebClient.Builder webClientBuilder;

	@Mock
	private WebClient webClient;

	@Mock
	private ExchangeFunction exchangeFunction;

	private WebClient.RequestHeadersSpec headersSpec;
	private WebClient.ResponseSpec responseSpec;

	@InjectMocks
	private KeystoneModule keystoneModule;

	@BeforeEach
	void setup() {
		WebClient mockedWebClient = WebClient.builder()
			.exchangeFunction(exchangeFunction)
			.build();
		keystoneModule = new KeystoneModule(mockedWebClient);
	}

	@BeforeAll
	static void initLogLevel() {
		LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
		loggerContext.getLogger("root").setLevel(Level.INFO);
	}

	@Test
	@DisplayName("이용자와 관리자는 Keystone Token을 사용하여 자신의 프로젝트 별 권한이 담긴 Permission Map을 얻을 수 있다")
	// TODO: Phase 2에서 external 요청 전담 패키지 분리 후 WebClient mock을 WebClient 클래스매핑 형태로 변경
	void givenKeystoneToken_whenGetPermissionMethodIsCalled_thenReturnPermissionMap() {
		// A. given
		String keystoneToken = "14080769fe05e1f8b837fb43ca0f0ba4";
		String keystoneUserId = "313233";
		String keystoneUserName = "admin";

		// ㄴ response mock: non-test API (getTokenInfo())
		// ClientResponse tokenInfoMokeResponse = ClientResponse.create(HttpStatus.OK)
		// 	.header("Content-Type", "application/json")
		// 	.header("X-Subject-Token", keystoneToken)
		// 	.body("{\"token\": {\"audit_ids\": [\"mAjXQhiYRyKwkB4qygdLVg\"],\"expires_at\": \"2015-11-05T22:00:11.000000Z\",\"issued_at\": \"2015-11-05T21:00:33.819948Z\",\"methods\": [\"password\"],\"user\": {\"domain\": {\"id\": \"default\",\"name\": \"Default\"},\"id\": \"10a2e6e717a245d9acad3e5f97aeca3d\",\"name\": \"admin\",\"password_expires_at\": null}}}")
		// 	.build();
		// when(exchangeFunction.exchange(
		// 	argThat(request ->
		// 		request.url().getPath().equals(KeystoneRoutes.TOKEN_AUTH_DEFAULT) &&
		// 			request.method().equals(HttpMethod.POST)
		// 	)
		// ))
		// .thenReturn(Mono.just(tokenInfoMokeResponse));

		// ㄴ response mock: keystone api
		// ClientResponse mockResponse = ClientResponse.create(HttpStatus.OK)
		// 	.header("Content-Type", "application/json")
		// 	.header("X-Subject-Token", keystoneToken)
		// 	.body("{\"role_assignments\": [{\"role\": {\"domain\": {\"id\": \"161718\",\"name\": \"Default\"},\"id\": \"123456\",\"name\": \"admin\"},\"scope\": {\"domain\": {\"id\": \"161718\",\"name\": \"Default\"}},\"user\": {\"domain\": {\"id\": \"161718\",\"name\": \"Default\"},\"id\": \"313233\",\"name\": \"admin\"}},{\"role\": {\"domain\": {\"id\": \"161718\",\"name\": \"Default\"},\"id\": \"123456\",\"name\": \"admin\"},\"scope\": {\"project\": {\"domain\": {\"id\": \"161718\",\"name\": \"Default\"},\"id\": \"456789\",\"name\": \"admin\"}},\"user\": {\"domain\": {\"id\": \"161718\",\"name\": \"Default\"},\"id\": \"313233\",\"name\": \"admin\"}}]}")
		// 	.build();
		// when(exchangeFunction.exchange(
		// 	argThat(request ->
		// 		request.url().getPath().equals(KeystoneRoutes.GET_ASSIGNED_PERMISSIONS) &&
		// 		request.method().equals(HttpMethod.GET)
		// 	)
		// ))
		// .thenReturn(Mono.just(mockResponse));

		// B. when
		// Map<String, ProjectPermission> permissionMap = keystoneModule.getPermission(keystoneToken);
		// PrintUtil.printMap(permissionMap);

		// C. then
		// assertEquals(
		// 	keystoneToken,
		// 	generatedKeystoneToken
		// );
		assertEquals("a", "b");
	}

	@Test
	@DisplayName("이용자와 관리자는 Keycloak에서의 로그인 성공을 통해 발급된 authentication code를 사용해 Keystone에 로그인을 시도하여 KeystoneToken을 발급받을 수 있다")
	void givenKeycloakCode_whenLoginMethodIsCalled_thenReturnKeystoneToken() {
		// A. given
		String keycloakCode = "KEYCLOAK_TEST_TOKEN";
		String keystoneToken = "14080769fe05e1f8b837fb43ca0f0ba4";

		// ㄴ response mock: keystone api
		ClientResponse mockResponse = ClientResponse.create(HttpStatus.CREATED)
			.header("Content-Type", "application/json")
			.header("X-Subject-Token", keystoneToken)
			.body("{\"token\": {\"expires_at\": \"2025-08-15T10:30:27.000000Z\"}}")
			.build();
		when(exchangeFunction.exchange(any(ClientRequest.class)))
			.thenReturn(Mono.just(mockResponse));

		// B. when
		String generatedKeystoneToken = keystoneModule.login(keycloakCode);
		log.info("[Login] Token generated: {}", generatedKeystoneToken);

		// C. then
		assertEquals(
			keystoneToken,
			generatedKeystoneToken
		);
	}

	@Test
	@DisplayName("관리자는 ACC 요청자의 개인정보와 keystone 토큰을 이용해 Keystone의 사용자 계정을 생성할 수 있다.")
	void givenDomainUserAndKeystoneToken_whenCreateKeystoneUser_thenReturnKeystoneUserInfo() {
		// given
		User user = User.builder()
				.name("testUser")
				.email("test@example.com")
				.enabled(true)
				.build();
		String keystoneToken = "test-keystone-token";
		String createdUserId = "created-user-id";
		
		ClientResponse mockResponse = ClientResponse.create(HttpStatus.CREATED)
			.header("Content-Type", "application/json")
			.body("{\"user\": {\"id\": \"" + createdUserId + "\", \"name\": \"testUser\", \"email\": \"test@example.com\", \"enabled\": true}}")
			.build();
		when(exchangeFunction.exchange(any(ClientRequest.class)))
			.thenReturn(Mono.just(mockResponse));

		// when
		User createdUser = keystoneModule.createUser(user, keystoneToken);

		// then
		assertEquals(createdUserId, createdUser.getId());
		assertEquals("testUser", createdUser.getName());
		assertEquals("test@example.com", createdUser.getEmail());
		assertTrue(createdUser.isEnabled());
	}

	@Test
	@DisplayName("관리자와 사용자 본인은 keystone 토큰과 사용자 ID를 이용해 Keystone에서 사용자 상세 정보를 조회할 수 있다.")
	void givenUserIdAndKeystoneToken_whenGetUserDetail_thenReturnUserInfo() {
		// given
		String userId = "test-user-id";
		String keystoneToken = "test-keystone-token";
		
		ClientResponse mockResponse = ClientResponse.create(HttpStatus.OK)
			.header("Content-Type", "application/json")
			.body("{\"user\": {\"id\": \"" + userId + "\"," +
					" \"name\": \"testUser\"," +
					" \"email\": \"test@example.com\", " +
					"\"enabled\": true, " +
					"\"description\": \"test description\"}}")
			.build();
		when(exchangeFunction.exchange(any(ClientRequest.class)))
			.thenReturn(Mono.just(mockResponse));

		// when
		User userDetail = keystoneModule.getUserDetail(userId, keystoneToken);

		// then
		assertEquals(userId, userDetail.getId());
		assertEquals("testUser", userDetail.getName());
		assertEquals("test@example.com", userDetail.getEmail());
		assertEquals("test description", userDetail.getDescription());
		assertTrue(userDetail.isEnabled());
	}

	@Test
	@DisplayName("관리자와 사용자 본인은 keystone 토큰과 사용자 ID, 수정할 정보를 이용해 Keystone에서 사용자 정보를 업데이트할 수 있다.")
	void givenUserIdAndUserInfoAndKeystoneToken_whenUpdateUser_thenReturnUpdatedUserInfo() {
		// given
		String userId = "test-user-id";
		User user = User.builder()
				.name("updatedUser")
				.email("updated@example.com")
				.description("updated description")
				.enabled(true)
				.build();
		String keystoneToken = "test-keystone-token";
		
		ClientResponse mockResponse = ClientResponse.create(HttpStatus.OK)
			.header("Content-Type", "application/json")
			.body("{\"user\": {\"id\": \"" + userId + "\"," +
					" \"name\": \"updatedUser\", " +
					"\"email\": \"updated@example.com\", " +
					"\"description\": \"updated description\", " +
					"\"enabled\": true}}")
			.build();
		when(exchangeFunction.exchange(any(ClientRequest.class)))
			.thenReturn(Mono.just(mockResponse));

		// when
		User updatedUser = keystoneModule.updateUser(userId, user, keystoneToken);

		// then
		assertEquals(userId, updatedUser.getId());
		assertEquals("updatedUser", updatedUser.getName());
		assertEquals("updated@example.com", updatedUser.getEmail());
		assertEquals("updated description", updatedUser.getDescription());
		assertTrue(updatedUser.isEnabled());
	}

	@Test
	@DisplayName("관리자와 사용자 본인은 keystone 토큰과 사용자 ID를 이용해 Keystone에서 사용자를 삭제할 수 있다.")
	void givenUserIdAndKeystoneToken_whenDeleteUser_thenDeleteUserSuccessfully() {
		// given
		String userId = "test-user-id";
		String keystoneToken = "test-keystone-token";
		
		ClientResponse mockResponse = ClientResponse.create(HttpStatus.NO_CONTENT)
			.header("Content-Type", "application/json")
			.build();
		when(exchangeFunction.exchange(any(ClientRequest.class)))
			.thenReturn(Mono.just(mockResponse));

		// when & then
		assertDoesNotThrow(() -> {
			keystoneModule.deleteUser(userId, keystoneToken);
		});
	}

	@Test
	@DisplayName("관리자는 ACC 요청자의 프로젝트 정보와 keystone 토큰을 이용해 Keystone의 프로젝트를 생성할 수 있다.")
	void givenKeystoneProjectAndKeystoneToken_whenCreateKeystoneProject_thenReturnKeystoneProjectInfo() {
		// given
		KeystoneProject project = KeystoneProject.builder()
				.name("testProject")
				.description("test project description")
				.enabled(true)
				.isDomain(false)
				.build();
		String keystoneToken = "test-keystone-token";
		String createdProjectId = "created-project-id";
		
		ClientResponse mockResponse = ClientResponse.create(HttpStatus.CREATED)
			.header("Content-Type", "application/json")
			.body("{\"project\": {\"id\": \"" + createdProjectId + "\", \"name\": \"testProject\", \"description\": \"test project description\", \"enabled\": true, \"is_domain\": false}}")
			.build();
		when(exchangeFunction.exchange(any(ClientRequest.class)))
			.thenReturn(Mono.just(mockResponse));

		// when
		KeystoneProject createdProject = keystoneModule.createProject(project, keystoneToken);

		// then
		assertEquals(createdProjectId, createdProject.getId());
		assertEquals("testProject", createdProject.getName());
		assertEquals("test project description", createdProject.getDescription());
		assertTrue(createdProject.getEnabled());
		assertFalse(createdProject.getIsDomain());
	}

	@Test
	@DisplayName("관리자와 사용자는 keystone 토큰과 프로젝트 ID를 이용해 Keystone에서 프로젝트 상세 정보를 조회할 수 있다.")
	void givenProjectIdAndKeystoneToken_whenGetProjectDetail_thenReturnProjectInfo() {
		// given
		String projectId = "test-project-id";
		String keystoneToken = "test-keystone-token";
		
		ClientResponse mockResponse = ClientResponse.create(HttpStatus.OK)
			.header("Content-Type", "application/json")
			.body("{\"project\": {\"id\": \"" + projectId + "\"," +
					" \"name\": \"testProject\"," +
					" \"description\": \"test project description\", " +
					"\"enabled\": true, " +
					"\"is_domain\": false}}")
			.build();
		when(exchangeFunction.exchange(any(ClientRequest.class)))
			.thenReturn(Mono.just(mockResponse));

		// when
		KeystoneProject projectDetail = keystoneModule.getProjectDetail(projectId, keystoneToken);

		// then
		assertEquals(projectId, projectDetail.getId());
		assertEquals("testProject", projectDetail.getName());
		assertEquals("test project description", projectDetail.getDescription());
		assertTrue(projectDetail.getEnabled());
		assertFalse(projectDetail.getIsDomain());
	}

	@Test
	@DisplayName("관리자는 keystone 토큰과 프로젝트 ID, 수정할 정보를 이용해 Keystone에서 프로젝트 정보를 업데이트할 수 있다.")
	void givenProjectIdAndProjectInfoAndKeystoneToken_whenUpdateProject_thenReturnUpdatedProjectInfo() {
		// given
		String projectId = "test-project-id";
		KeystoneProject project = KeystoneProject.builder()
				.name("updatedProject")
				.description("updated project description")
				.enabled(true)
				.isDomain(false)
				.build();
		String keystoneToken = "test-keystone-token";
		
		ClientResponse mockResponse = ClientResponse.create(HttpStatus.OK)
			.header("Content-Type", "application/json")
			.body("{\"project\": {\"id\": \"" + projectId + "\"," +
					" \"name\": \"updatedProject\", " +
					"\"description\": \"updated project description\", " +
					"\"enabled\": true, " +
					"\"is_domain\": false}}")
			.build();
		when(exchangeFunction.exchange(any(ClientRequest.class)))
			.thenReturn(Mono.just(mockResponse));

		// when
		KeystoneProject updatedProject = keystoneModule.updateProject(projectId, project, keystoneToken);

		// then
		assertEquals(projectId, updatedProject.getId());
		assertEquals("updatedProject", updatedProject.getName());
		assertEquals("updated project description", updatedProject.getDescription());
		assertTrue(updatedProject.getEnabled());
		assertFalse(updatedProject.getIsDomain());
	}

	@Test
	@DisplayName("관리자는 keystone 토큰과 프로젝트 ID를 이용해 Keystone에서 프로젝트를 삭제할 수 있다.")
	void givenProjectIdAndKeystoneToken_whenDeleteProject_thenDeleteProjectSuccessfully() {
		// given
		String projectId = "test-project-id";
		String keystoneToken = "test-keystone-token";
		
		ClientResponse mockResponse = ClientResponse.create(HttpStatus.NO_CONTENT)
			.header("Content-Type", "application/json")
			.build();
		when(exchangeFunction.exchange(any(ClientRequest.class)))
			.thenReturn(Mono.just(mockResponse));

		// when & then
		assertDoesNotThrow(() -> {
			keystoneModule.deleteProject(projectId, keystoneToken);
		});
	}

}
