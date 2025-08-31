package com.acc.local.service.modules.auth;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

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
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFunction;
import org.springframework.web.reactive.function.client.WebClient;

import com.acc.global.util.PrintUtil;
import com.acc.local.domain.enums.ProjectPermission;
import com.acc.local.dto.auth.KeystoneTokenInfo;
import com.acc.local.service.modules.auth.constant.KeystoneRoutes;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import jdk.jfr.Description;
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
}
