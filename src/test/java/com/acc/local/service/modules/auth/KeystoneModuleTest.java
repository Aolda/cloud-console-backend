package com.acc.local.service.modules.auth;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.HashMap;
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
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFunction;
import org.springframework.web.reactive.function.client.WebClient;

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
	void getPermission() {
	}

	@Test
	@DisplayName("이용자와 관리자는 자신의 ID와 PW를 사용하여 통합로그인을 할 수 있다")
	void loginTest1() {
		// A. given
		String keycloakToken = "KEYCLOAK_TEST_TOKEN";
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
		String generatedKeystoneToken = keystoneModule.login(keycloakToken).block();
		log.info("[Login] Token generated: {}", generatedKeystoneToken);

		// C. then
		assertEquals(
			keystoneToken,
			generatedKeystoneToken
		);
	}
}
