package com.acc.local.service.modules.auth;

import com.acc.global.exception.auth.AuthErrorCode;
import com.acc.global.exception.auth.KeystoneManagementException;
import com.acc.local.domain.model.KeystoneProject;
import com.acc.local.domain.model.User;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import com.acc.local.domain.enums.ProjectPermission;
import com.acc.local.dto.auth.KeystoneTokenInfo;
import com.acc.local.external.modules.keystone.constant.KeystoneRoutes;

import java.time.LocalDateTime;
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
		Map<String, Map<String, ?>> openstackPermissionList = requestOpenstackAccountPermissionList(
			getTokenInfo(keystoneToken).userId(),
			keystoneToken
		);
		return createUserPermissionMap(openstackPermissionList);
	}

	public ProjectPermission getPermission(String keystoneToken, String projectName) {
		Map<String, ProjectPermission> permission = getPermission(keystoneToken);
		return permission.getOrDefault(projectName, ProjectPermission.NONE);
	}

	public KeystoneTokenInfo getTokenInfo(String keystoneToken) {
		Map<String, Map<String, ?>> openstackTokenInfo = requestOpenstackTokenInfo(keystoneToken);
		return extractKeystoneTokenInfo(openstackTokenInfo);
	}

	public String login(String keycloakCode) {
		Map<String, Map<String, ?>> keystoneResponse = requestFederateLogin(keycloakCode);
		return extractKeystoneToken(keystoneResponse);
	}

	public User createUser(User user, String keystoneToken) {
		Map<String, Object> userRequest = createKeystoneUserRequest(user);

		try {
			Map<String, Map<String, ?>> response = requestKeystonePost(
					KeystoneRoutes.CREATE_USER,
					userRequest,
					createKeystoneAPIRequestHeader(keystoneToken)
			).block();

			log.info("User created successfully in Keystone: {}", user.getName());

			return parseKeystoneUserResponse(response);

		} catch (Exception e) {
			log.error("Failed to create user in Keystone: {}, Error: {}", user.getName(), e.getMessage());
			throw new KeystoneManagementException(
					AuthErrorCode.KEYSTONE_USER_CREATION_FAILED,
					"Keystone 사용자 생성 실패: " + user.getName(),
					e
			);
		}
	}

	public User getUserDetail(String userId, String keystoneToken) {
		try {
			Map<String, Map<String, ?>> response = requestKeystoneGet(
					KeystoneRoutes.POST_USER.replace("{user_id}", userId),
					Map.of(),
					createKeystoneAPIRequestHeader(keystoneToken)
			).block();

			log.info("User retrieved successfully from Keystone: {}", userId);

			return parseKeystoneUserResponse(response);

		} catch (Exception e) {
			log.error("Failed to get user from Keystone: {}, Error: {}", userId, e.getMessage());
			throw new KeystoneManagementException(
					AuthErrorCode.KEYSTONE_USER_CREATION_FAILED,
					"Keystone 사용자 조회 실패: " + userId,
					e
			);
		}
	}

	public User updateUser(String userId, User user, String keystoneToken) {
		Map<String, Object> userRequest = createKeystoneUpdateUserRequest(user);

		try {
			Map<String, Map<String, ?>> response = requestKeystonePost(
					KeystoneRoutes.POST_USER.replace("{user_id}", userId),
					userRequest,
					createKeystoneAPIRequestHeader(keystoneToken)
			).block();

			log.info("User updated successfully in Keystone: {}", userId);

			return parseKeystoneUserResponse(response);

		} catch (Exception e) {
			log.error("Failed to update user in Keystone: {}, Error: {}", userId, e.getMessage());
			throw new KeystoneManagementException(
					AuthErrorCode.KEYSTONE_USER_CREATION_FAILED,
					"Keystone 사용자 업데이트 실패: " + userId,
					e
			);
		}
	}

	public void deleteUser(String userId, String keystoneToken) {
		try {
			requestKeystoneDelete(
					KeystoneRoutes.POST_USER.replace("{user_id}", userId),
					createKeystoneAPIRequestHeader(keystoneToken)
			).block();


		} catch (Exception e) {

			throw new KeystoneManagementException(
					AuthErrorCode.KEYSTONE_USER_CREATION_FAILED,
					"Keystone 사용자 삭제 실패: " + userId,
					e
			);
		}
	}

	public KeystoneProject createProject(KeystoneProject project, String keystoneToken) {
		Map<String, Object> projectRequest = createKeystoneProjectRequest(project);

		try {
			Map<String, Map<String, ?>> response = requestKeystonePost(
					KeystoneRoutes.CREATE_PROJECT,
					projectRequest,
					createKeystoneAPIRequestHeader(keystoneToken)
			).block();

			return parseKeystoneProjectResponse(response);

		} catch (Exception e) {
			throw new KeystoneManagementException(
					AuthErrorCode.KEYSTONE_PROJECT_CREATION_FAILED,
					"Keystone 프로젝트 생성 실패: " + project.getName(),
					e
			);
		}
	}

	public KeystoneProject getProjectDetail(String projectId, String keystoneToken) {
		try {
			Map<String, Map<String, ?>> response = requestKeystoneGet(
					KeystoneRoutes.POST_PROJECT.replace("{project_id}", projectId),
					Map.of(),
					createKeystoneAPIRequestHeader(keystoneToken)
			).block();

			return parseKeystoneProjectResponse(response);

		} catch (Exception e) {
			throw new KeystoneManagementException(
					AuthErrorCode.KEYSTONE_PROJECT_RETRIEVAL_FAILED,
					"Keystone 프로젝트 조회 실패: " + projectId,
					e
			);
		}
	}

	public KeystoneProject updateProject(String projectId, KeystoneProject project, String keystoneToken) {
		Map<String, Object> projectRequest = createKeystoneUpdateProjectRequest(project);

		try {
			Map<String, Map<String, ?>> response = requestKeystonePatch(
					KeystoneRoutes.POST_PROJECT.replace("{project_id}", projectId),
					projectRequest,
					createKeystoneAPIRequestHeader(keystoneToken)
			).block();

			return parseKeystoneProjectResponse(response);

		} catch (Exception e) {
			throw new KeystoneManagementException(
					AuthErrorCode.KEYSTONE_PROJECT_UPDATE_FAILED,
					"Keystone 프로젝트 업데이트 실패: " + projectId,
					e
			);
		}
	}

	public void deleteProject(String projectId, String keystoneToken) {
		try {
			requestKeystoneDelete(
					KeystoneRoutes.POST_PROJECT.replace("{project_id}", projectId),
					createKeystoneAPIRequestHeader(keystoneToken)
			).block();

		} catch (Exception e) {
			throw new KeystoneManagementException(
					AuthErrorCode.KEYSTONE_PROJECT_DELETION_FAILED,
					"Keystone 프로젝트 삭제 실패: " + projectId,
					e
			);
		}
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

	private Map<String, Map<String, ?>> requestOpenstackTokenInfo(String keystoneToken) {
		return requestKeystoneGet(
			String.format(KeystoneRoutes.TOKEN_AUTH_DEFAULT),
			new HashMap<>(),
			createKeystoneAPIRequestHeader(keystoneToken)
		).block();
	}

	private Map<String, Map<String, ?>> requestOpenstackAccountPermissionList(String keystoneUserId, String keystoneToken) {
		return requestKeystoneGet(
			String.format(KeystoneRoutes.GET_ASSIGNED_PERMISSIONS, keystoneUserId),
			new HashMap<>(),
			createKeystoneAPIRequestHeader(keystoneToken)
		).block();
	}

	private Map<String, Map<String, ?>> requestFederateLogin(String keycloakCode) {
		return requestKeystonePost(
			KeystoneRoutes.TOKEN_AUTH_FEDERATE,
			new HashMap<>(),
			createKeystoneLoginHeader(keycloakCode)
		).block();
	}

	private String extractKeystoneToken(Map<String, Map<String, ?>> keystoneResponse) {
		Map<String, ?> headers = keystoneResponse.get("headers");
		return String.valueOf(headers.get("X-Subject-Token")).trim();
	}

	private KeystoneTokenInfo extractKeystoneTokenInfo(Map<String, Map<String, ?>> openstackTokenInfo) {
		Map<String, ?> body = openstackTokenInfo.get("body");
		return new KeystoneTokenInfo(
			getObjectValue(body, "token.audit_ids"),
			LocalDateTime.parse(String.valueOf(getObjectValue(body, "token.expires_at")).split("\\.")[0]),
			LocalDateTime.parse(String.valueOf(getObjectValue(body, "token.issued_at")).split("\\.")[0]),
			getObjectValue(body, "token.user.id"),
			getObjectValue(body, "token.user.name")
		);
	}

	private User parseKeystoneUserResponse(Map<String, Map<String, ?>> response) {
		Map<String, ?> body = response.get("body");
		if (body != null && body.containsKey("user")) {
			Map<String, Object> userObject = (Map<String, Object>) body.get("user");

			return User.builder()
					.id((String) userObject.get("id"))
					.name((String) userObject.get("name"))
					.domainId((String) userObject.get("domain_id"))
					.defaultProjectId((String) userObject.get("default_project_id"))
					.enabled((Boolean) userObject.getOrDefault("enabled", false))
					.email((String) userObject.get("email"))
					.description((String) userObject.get("description"))
					.federated((List<Map<String, Object>>) userObject.get("federated"))
					.links((Map<String, String>) userObject.get("links"))
					.passwordExpiresAt((String) userObject.get("password_expires_at"))
					.options((Map<String, Object>) userObject.get("options"))
					.build();
		}

		return User.builder()
				.enabled(false)
				.build();
	}

	private Map<String, Object> createKeystoneUserRequest(User user) {
		Map<String, Object> userObject = new HashMap<>();
		userObject.put("name", user.getName());
		userObject.put("password", user.getPassword());
		userObject.put("enabled", user.isEnabled());
		userObject.put("email", user.getEmail());

		Map<String, Object> request = new HashMap<>();
		request.put("user", userObject);

		return request;
	}


	private Map<String, Object> createKeystoneUpdateUserRequest(User user) {
		Map<String, Object> userObject = new HashMap<>();

		if (user.getName() != null) {
			userObject.put("name", user.getName());
		}
		if (user.getEmail() != null) {
			userObject.put("email", user.getEmail());
		}
		if (user.getDescription() != null) {
			userObject.put("description", user.getDescription());
		}
		if (user.getDefaultProjectId() != null) {
			userObject.put("default_project_id", user.getDefaultProjectId());
		}
		userObject.put("enabled", user.isEnabled());

		Map<String, Object> request = new HashMap<>();
		request.put("user", userObject);

		return request;
	}

	private KeystoneProject parseKeystoneProjectResponse(Map<String, Map<String, ?>> response) {
		Map<String, ?> body = response.get("body");
		if (body != null && body.containsKey("project")) {
			Map<String, Object> projectObject = (Map<String, Object>) body.get("project");

			return KeystoneProject.builder()
					.id((String) projectObject.get("id"))
					.name((String) projectObject.get("name"))
					.description((String) projectObject.get("description"))
					.domainId((String) projectObject.get("domain_id"))
					.enabled((Boolean) projectObject.getOrDefault("enabled", false))
					.isDomain((Boolean) projectObject.getOrDefault("is_domain", false))
					.parentId((String) projectObject.get("parent_id"))
					.tags((List<String>) projectObject.get("tags"))
					.links((Map<String, Object>) projectObject.get("links"))
					.options((Map<String, Object>) projectObject.get("options"))
					.build();
		}

		return KeystoneProject.builder()
				.enabled(false)
				.build();
	}

	private Map<String, Object> createKeystoneProjectRequest(KeystoneProject project) {
		Map<String, Object> projectObject = new HashMap<>();
		projectObject.put("name", project.getName());
		projectObject.put("enabled", project.getEnabled());
		projectObject.put("is_domain", project.getIsDomain());
		
		if (project.getDescription() != null) {
			projectObject.put("description", project.getDescription());
		}
		if (project.getDomainId() != null) {
			projectObject.put("domain_id", project.getDomainId());
		}
		if (project.getParentId() != null) {
			projectObject.put("parent_id", project.getParentId());
		}
		if (project.getTags() != null) {
			projectObject.put("tags", project.getTags());
		}
		if (project.getOptions() != null) {
			projectObject.put("options", project.getOptions());
		}

		Map<String, Object> request = new HashMap<>();
		request.put("project", projectObject);

		return request;
	}

	private Map<String, Object> createKeystoneUpdateProjectRequest(KeystoneProject project) {
		Map<String, Object> projectObject = new HashMap<>();
		
		if (project.getName() != null) {
			projectObject.put("name", project.getName());
		}
		if (project.getDescription() != null) {
			projectObject.put("description", project.getDescription());
		}
		if (project.getDomainId() != null) {
			projectObject.put("domain_id", project.getDomainId());
		}
		if (project.getEnabled() != null) {
			projectObject.put("enabled", project.getEnabled());
		}
		if (project.getIsDomain() != null) {
			projectObject.put("is_domain", project.getIsDomain());
		}
		if (project.getTags() != null) {
			projectObject.put("tags", project.getTags());
		}
		if (project.getOptions() != null) {
			projectObject.put("options", project.getOptions());
		}

		Map<String, Object> request = new HashMap<>();
		request.put("project", projectObject);

		return request;
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

	private Mono<Map<String, Map<String, ?>>> requestKeystoneDelete(String path, Map<String, String> headers) {
		return keystoneWebClient.delete()
			.uri(path)
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

	private Mono<Map<String, Map<String, ?>>> requestKeystonePatch(String path, Map<String, Object> body, Map<String, String> headers) {
		return keystoneWebClient.patch()
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
