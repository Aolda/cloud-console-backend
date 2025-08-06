package com.acc.local.service.modules.auth;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import com.acc.local.domain.enums.ProjectPermission;
import com.acc.local.service.modules.auth.constant.KeystoneRoutes;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
@Slf4j
public class KeystoneModule {

	@Value("${server.protocol}")
	private String SERVICE_PROTO;
	@Value("${server.domain}")
	private String SERVICE_DOMAIN;
	@Value("${server.port}")
	private String SERVICE_PORT;

	private final WebClient keystoneWebClient;

	public Mono<String> login(String keycloakCode) {
		Mono<Map<String, Map<String, ?>>> keystoneResponse = requestKeystonePost(
			KeystoneRoutes.TOKEN_AUTH,
			new HashMap<>(),
			createKeystoneLoginHeader(keycloakCode)
		);
		return extractKeystoneToken(keystoneResponse);
	}

	private Mono<String> extractKeystoneToken(Mono<HashMap<String, Object>> keystoneResponse) {
			return keystoneResponse.map(response -> response.get("X-Subject-Token").toString());
	/* ==== [Task] Util Methods ==== */
	}

	private Mono<HashMap<String, Object>> requestKeystonePost(String path, Map<String, Object> body, Map<String, String> headers) {
	private Mono<String> extractKeystoneToken(Mono<Map<String, Map<String, ?>>> keystoneResponse) {
		return keystoneResponse.map(response -> {
			Map<String, ?> headers = response.get("headers");
			return String.valueOf(headers.get("X-Subject-Token")).trim();
		});
	}

	/* ==== [WebClient] Request Methods ==== */

	private Mono<Map<String, Map<String, ?>>> requestKeystoneGet(String path, Map<String, Object> queries, Map<String, String> headers) {
	    return keystoneWebClient.get()
	        .uri(uriBuilder -> {
	            uriBuilder.path(path);
	            if (queries != null) queries.forEach(uriBuilder::queryParam);
	            return uriBuilder.build();
	        })
	        .headers(httpHeaders -> headers.forEach(httpHeaders::add))
			.exchangeToMono(response -> response.bodyToMono(Map.class)
				.defaultIfEmpty(new HashMap<String, Map<String, ?>>())
				.map(responsedBody -> collectWebClientResponse(
						response.headers().asHttpHeaders(),
						responsedBody
					)
				)
			);
	}

	private Mono<Map<String, Map<String, ?>>> requestKeystonePost(String path, Map<String, Object> body, Map<String, String> headers) {
		return keystoneWebClient.post()
			.uri(path)
			.bodyValue(body)
			.headers(httpHeaders -> headers.forEach(httpHeaders::add))
			.exchangeToMono(response -> response.bodyToMono(Map.class)
				.defaultIfEmpty(new HashMap<String, Map<String, ?>>())
				.map(responsedBody -> collectWebClientResponse(
						response.headers().asHttpHeaders(),
						responsedBody
					)
				)
			);
	}

	private static Map<String, Map<String, ?>> collectWebClientResponse(HttpHeaders headers, Map body) {
		Map<String, Map<String, ?>> returnResponse = new HashMap<>();

		Map<String, String> headerMap = new HashMap<>();
		headers.forEach((k, v) -> headerMap.put(k, String.join(",", v)));
		returnResponse.put("headers", headerMap);

		returnResponse.put("body", body);

		return returnResponse;
	}

	private Map<String, String> createKeystoneLoginHeader(String keycloakCode) {
		Map<String, String> headers = new HashMap<>();
		headers.put("Authorization", "Bearer " + keycloakCode);
		headers.put("Content-Type", "application/json");
		return headers;
	}

}
