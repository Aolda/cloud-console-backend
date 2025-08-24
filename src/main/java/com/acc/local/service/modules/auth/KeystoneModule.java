package com.acc.local.service.modules.auth;

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

	private final WebClient keystoneWebClient;

	public Map<String, ProjectPermission> getPermission(String keystoneToken) {
		Map<String, Map<String, ?>> openstackPermissionList = getOpenstackAccountPermissionList(keystoneToken);
		return createUserPermissionMap(openstackPermissionList);
	}

	public ProjectPermission getPermission(String keystoneToken, String projectName) {
		Map<String, ProjectPermission> permission = getPermission(keystoneToken);
		return permission.getOrDefault(projectName, ProjectPermission.NONE);
	}

	public String login(String keycloakCode) {
		Map<String, Map<String, ?>> keystoneResponse = requestKeystonePost(
			KeystoneRoutes.TOKEN_AUTH,
			new HashMap<>(),
			createKeystoneLoginHeader(keycloakCode)
		).block();
		return extractKeystoneToken(keystoneResponse);
	}

	/* ==== [Task] Util Methods ==== */

	private static Map<String, ProjectPermission> createUserPermissionMap(Map<String, Map<String, ?>> permissionData) {
		List<Map<String, Object>> roles = (List<Map<String, Object>>)permissionData.get("role_assignments");
		return roles.stream()
			.map(KeystoneModule::parseAssignedRoles)
			.filter(Objects::nonNull)
			.map(roleInfo -> Map.entry(
				roleInfo.getFirst(),
				ProjectPermission.findByKeystoneRoleName(roleInfo.getLast())
			))
			.collect(Collectors.toMap(
				Map.Entry::getKey,
				Map.Entry::getValue
			));
	}

	private static List<String> parseAssignedRoles(Map<String, Object> assignedRole) {
		try {
			String projectId = getObjectValue(assignedRole, "scope.project.id");
			String keystoneRoleName = getObjectValue(assignedRole, "role.name");

			return Arrays.asList(projectId, keystoneRoleName);
		} catch (NullPointerException e) {
			log.warn(e.getMessage());
		}

		return null;
	}

	private static <T> T getObjectValue(Object object, String keys) {
		if (!(object instanceof Map)) return null;

		int splitIndex = keys.indexOf('.');
		String key = keys.substring(0, splitIndex);

		Map<String, Object> mapObject = (Map<String, Object>) object;
		if (!mapObject.containsKey(key)) return null;
		T value = (T) mapObject.get(key);

		if (keys.indexOf('.', splitIndex + 1) == -1) return value;
		return getObjectValue(value, keys.substring(splitIndex));
	}

	private Map<String, Map<String, ?>> getOpenstackAccountPermissionList(String keystoneToken) {
		return requestKeystoneGet(
			KeystoneRoutes.GET_ASSIGNED_PERMISSIONS,
			new HashMap<>(),
			createKeystoneAPIRequestHeader(keystoneToken)
		).block();
	}

	private String extractKeystoneToken(Map<String, Map<String, ?>> keystoneResponse) {
		Map<String, ?> headers = keystoneResponse.get("headers");
		return String.valueOf(headers.get("X-Subject-Token")).trim();
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

	private Map<String, String> createKeystoneAPIRequestHeader(String keystoneToken) {
		Map<String, String> headers = new HashMap<>();
		headers.put("X-Auth-Token", keystoneToken);
		headers.put("Content-Type", "application/json");
		return headers;
	}

}
