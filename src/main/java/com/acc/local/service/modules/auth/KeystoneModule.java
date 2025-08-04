package com.acc.local.service.modules.auth;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import com.acc.global.exception.AccBaseException;
import com.acc.local.service.modules.auth.constant.KeystoneRoutes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class KeystoneModule {

	@Value("${server.protocol}")
	private String SERVICE_PROTO;
	@Value("${server.domain}")
	private String SERVICE_DOMAIN;
	@Value("${server.port}")
	private String SERVICE_PORT;

	private final WebClient keystoneWebClient;

	public Mono<String> login(String keycloakCode) throws AccBaseException {
		Mono<HashMap<String, Object>> keystoneResponse = requestKeystonePost(
			KeystoneRoutes.TOKEN_AUTH,
			new HashMap<>(),
			createKeystoneLoginHeader(keycloakCode)
		);
		return extractKeystoneToken(keystoneResponse);
	}

	private Mono<String> extractKeystoneToken(Mono<HashMap<String, Object>> keystoneResponse) {
			return keystoneResponse.map(response -> response.get("X-Subject-Token").toString());
	}

	private Mono<HashMap<String, Object>> requestKeystonePost(String path, Map<String, Object> body, Map<String, String> headers) {
		return keystoneWebClient.post()
			.uri(path)
			.bodyValue(body)
			.headers(httpHeaders -> headers.forEach(httpHeaders::add))
			.retrieve()
			.bodyToMono(Map.class)
			.map(HashMap<String, Object>::new);
	}

	private Map<String, String> createKeystoneLoginHeader(String keycloakCode) {
		Map<String, String> headers = new HashMap<>();
		headers.put("Authorization", "Bearer " + keycloakCode);
		headers.put("Content-Type", "application/json");
		return headers;
	}

}
