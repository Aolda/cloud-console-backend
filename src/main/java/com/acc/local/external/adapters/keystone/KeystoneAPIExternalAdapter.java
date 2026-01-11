package com.acc.local.external.adapters.keystone;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.acc.global.exception.auth.AuthServiceException;
import com.acc.local.domain.enums.project.ProjectRole;
import com.acc.local.domain.model.auth.Role;
import com.acc.local.domain.model.auth.RoleAssignmentListResponse;
import com.acc.local.domain.model.auth.RoleListResponse;
import com.acc.local.dto.auth.UserKeystoneDto;
import com.acc.local.domain.model.auth.UserListResponse;
import com.acc.local.external.dto.keystone.*;
import org.hibernate.sql.Update;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import com.acc.global.common.PageRequest;
import com.acc.global.exception.AccBaseException;
import com.acc.global.exception.auth.AuthErrorCode;
import com.acc.global.exception.auth.JwtAuthenticationException;
import com.acc.global.exception.auth.KeystoneException;
import com.acc.local.dto.project.ProjectListDto;
import com.acc.local.dto.auth.KeystonePasswordLoginRequest;
import com.acc.local.dto.auth.KeystoneToken;
import com.acc.local.external.dto.OpenstackPagination;
import com.acc.local.external.modules.keystone.KeystoneAPIUtils;
import com.acc.local.external.modules.keystone.KeystoneAuthAPIModule;
import com.acc.local.external.modules.keystone.KeystoneProjectAPIModule;
import com.acc.local.external.modules.keystone.KeystoneUserAPIModule;
import com.acc.local.external.modules.keystone.KeystoneRoleAPIModule;
import com.acc.local.external.ports.KeystoneAPIExternalPort;
import com.fasterxml.jackson.databind.JsonNode;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class KeystoneAPIExternalAdapter implements KeystoneAPIExternalPort {

	private final KeystoneAuthAPIModule keystoneAuthAPIModule;
	private final KeystoneProjectAPIModule keystoneProjectAPIModule;
	private final KeystoneUserAPIModule keystoneUserAPIModule;
	private final KeystoneRoleAPIModule keystoneRoleAPIModule;

	@Override
	public KeystoneToken getUnscopedToken(KeystonePasswordLoginRequest loginRequest) throws AccBaseException {
		ResponseEntity<JsonNode> loginResponse = authenticateKeystoneByPassword(loginRequest);
		return KeystoneAPIUtils.extractKeystoneToken(loginResponse);
	}

	@Override
	public KeystoneToken getUnscopedTokenByToken(String existingToken) throws AccBaseException {
		ResponseEntity<JsonNode> tokenResponse = authenticateKeystoneByToken(existingToken);
		return KeystoneAPIUtils.extractKeystoneToken(tokenResponse);
	}

	@Override
	public KeystoneToken getScopedToken(String projectId, String unscopedToken) throws AccBaseException {
		ResponseEntity<JsonNode> tokenResponse = authenticateKeystoneWithScope(projectId, unscopedToken);
		return KeystoneAPIUtils.extractKeystoneToken(tokenResponse);
	}

	@Override
	public KeystoneToken getAdminToken(KeystonePasswordLoginRequest loginRequest) throws AccBaseException {
		KeystoneToken unscopedToken = getUnscopedToken(loginRequest);
		// log.info("unscopedToken: {}", unscopedToken);
		ResponseEntity<JsonNode> systemAdminTokenResponse = authenticateKeystoneWithSystemPrivilege(unscopedToken.token());
		// log.info("systemAdminTokenResponse: {}", systemAdminTokenResponse);
		return KeystoneAPIUtils.extractKeystoneToken(systemAdminTokenResponse);
	}

	@Override
	public KeystoneToken getAdminTokenWithAdminProjectScope(KeystonePasswordLoginRequest loginRequest) throws AccBaseException {
		KeystoneToken unscopedToken = getUnscopedToken(loginRequest);
		KeystoneToken adminSystemScopedToken = getAdminToken(loginRequest);
		String adminProjectId = getAdminProjectId(adminSystemScopedToken.token());
		ResponseEntity<JsonNode> systemAdminTokenResponse = authenticateKeystoneWithAdminProjectPrivilege(unscopedToken.token(), adminProjectId);

		revokeToken(adminSystemScopedToken.token());
		return KeystoneAPIUtils.extractKeystoneToken(systemAdminTokenResponse);
	}

	@Override
	public void revokeToken(String keystoneToken) {
		try {
			keystoneAuthAPIModule.revokeToken(keystoneToken);
		} catch (WebClientResponseException e) {}
	}

	@Override
	public KeystoneToken getTokenObject(String keystoneToken) throws AccBaseException {
		ResponseEntity<JsonNode> tokenInfoResponse = requestKeystoneTokenInfo(keystoneToken);
		return KeystoneAPIUtils.extractKeystoneToken(tokenInfoResponse);
	}

	@Override
	public ResponseEntity<JsonNode> requestFederateLogin(String keycloakCode) {
		try {
			return keystoneAuthAPIModule.requestFederateLogin(keycloakCode);
		} catch (WebClientResponseException e) {
			HttpStatusCode status = e.getStatusCode();
			if (status == HttpStatus.UNAUTHORIZED) {
				throw new KeystoneException(AuthErrorCode.UNAUTHORIZED, e);
			} else if (status == HttpStatus.FORBIDDEN) {
				throw new KeystoneException(AuthErrorCode.FORBIDDEN_ACCESS, e);
			} else if (status == HttpStatus.BAD_REQUEST) {
				throw new KeystoneException(AuthErrorCode.INVALID_REQUEST_PARAMETER, e);
			}
			throw new KeystoneException(AuthErrorCode.KEYSTONE_API_FAILURE, e);
		}
	}

	@Override
	public KeystoneToken getTokenInfo(String token) {
		try {
            log.info("token - 시스템 어드민  {}" ,token);
			ResponseEntity<JsonNode> keystoneResponse = keystoneAuthAPIModule.getTokenInfo(token);

			if (keystoneResponse == null) {
				throw new JwtAuthenticationException(AuthErrorCode.KEYSTONE_TOKEN_AUTHENTICATION_FAILED);
			}

			return KeystoneAPIUtils.extractKeystoneToken(keystoneResponse);
		} catch (WebClientResponseException e) {
			HttpStatusCode status = e.getStatusCode();
			if (status == HttpStatus.UNAUTHORIZED) {
				throw new KeystoneException(AuthErrorCode.UNAUTHORIZED, e);
			} else if (status == HttpStatus.FORBIDDEN) {
				throw new KeystoneException(AuthErrorCode.FORBIDDEN_ACCESS, e);
			} else if (status == HttpStatus.NOT_FOUND) {
				throw new KeystoneException(AuthErrorCode.KEYSTONE_TOKEN_AUTHENTICATION_FAILED, e);
			}
			throw new KeystoneException(AuthErrorCode.KEYSTONE_API_FAILURE, e);
		}
	}

	// ----- User -----

	@Override
	public UserKeystoneDto createUser(String token, CreateKeystoneUserRequest createUserRequest) {
		try {
			ResponseEntity<JsonNode> keystoneResponse = keystoneUserAPIModule.createUser(token, createUserRequest.toKeystoneRequest());
			if (keystoneResponse == null) {
				throw new AuthServiceException(AuthErrorCode.KEYSTONE_USER_CREATION_FAILED, "사용자 생성 응답이 null입니다.");
			}

			return KeystoneAPIUtils.parseKeystoneUserResponse(keystoneResponse);
		} catch (WebClientResponseException e) {
			HttpStatusCode status = e.getStatusCode();
			if (status == HttpStatus.UNAUTHORIZED) {
				throw new KeystoneException(AuthErrorCode.UNAUTHORIZED, e);
			} else if (status == HttpStatus.FORBIDDEN) {
				throw new KeystoneException(AuthErrorCode.FORBIDDEN_ACCESS, e);
			} else if (status == HttpStatus.BAD_REQUEST) {
				throw new KeystoneException(AuthErrorCode.INVALID_REQUEST_PARAMETER, e);
			} else if (status == HttpStatus.CONFLICT) {
				throw new KeystoneException(AuthErrorCode.CONFLICT, e);
			}
			throw new KeystoneException(AuthErrorCode.KEYSTONE_USER_CREATION_FAILED, e);
		}
	}

	@Override
	public UserKeystoneDto getUserDetail(String userId, String token) {
		try {
			ResponseEntity<JsonNode> keystoneResponse = keystoneUserAPIModule.getUserDetail(userId, token);
			if (keystoneResponse == null) {
				throw new AuthServiceException(AuthErrorCode.USER_NOT_FOUND, "사용자를 찾을 수 없습니다.");
			}

			return KeystoneAPIUtils.parseKeystoneUserResponse(keystoneResponse);
		} catch (WebClientResponseException e) {
			HttpStatusCode status = e.getStatusCode();
			if (status == HttpStatus.UNAUTHORIZED) {
				throw new KeystoneException(AuthErrorCode.UNAUTHORIZED, e);
			} else if (status == HttpStatus.FORBIDDEN) {
				throw new KeystoneException(AuthErrorCode.FORBIDDEN_ACCESS, e);
			} else if (status == HttpStatus.NOT_FOUND) {
				throw new KeystoneException(AuthErrorCode.USER_NOT_FOUND, e);
			}
			throw new KeystoneException(AuthErrorCode.KEYSTONE_API_FAILURE, e);
		}
	}

	@Override
	public UserKeystoneDto updateUser(String userId, String token, UpdateKeystoneUserRequest userRequest) {
		try {
			log.info("Keystone updateUser 요청 - userId: {}, request: {}", userId, userRequest);

			ResponseEntity<JsonNode> keystoneResponse = keystoneUserAPIModule.updateUser(userId, token, userRequest.toKeystoneRequest());
			if (keystoneResponse == null) {
				throw new AuthServiceException(AuthErrorCode.KEYSTONE_USER_CREATION_FAILED, "사용자 업데이트 응답이 null입니다.");
			}

			return KeystoneAPIUtils.parseKeystoneUserResponse(keystoneResponse);
		} catch (WebClientResponseException e) {
			log.error("Keystone updateUser 실패 - userId: {}, status: {}, response: {}",
					userId, e.getStatusCode(), e.getResponseBodyAsString());
			HttpStatusCode status = e.getStatusCode();
			if (status == HttpStatus.UNAUTHORIZED) {
				throw new KeystoneException(AuthErrorCode.UNAUTHORIZED, e);
			} else if (status == HttpStatus.FORBIDDEN) {
				throw new KeystoneException(AuthErrorCode.FORBIDDEN_ACCESS, e);
			} else if (status == HttpStatus.NOT_FOUND) {
				throw new KeystoneException(AuthErrorCode.USER_NOT_FOUND, e);
			} else if (status == HttpStatus.BAD_REQUEST) {
				throw new KeystoneException(AuthErrorCode.INVALID_REQUEST_PARAMETER, e);
			} else if (status == HttpStatus.CONFLICT) {
				throw new KeystoneException(AuthErrorCode.CONFLICT, e);
			}
			throw new KeystoneException(AuthErrorCode.KEYSTONE_API_FAILURE, e);
		}
	}

	@Override
	public void deleteUser(String userId, String token) {
		try {
			keystoneUserAPIModule.deleteUser(userId, token);
		} catch (WebClientResponseException e) {
			HttpStatusCode status = e.getStatusCode();
			if (status == HttpStatus.UNAUTHORIZED) {
				throw new KeystoneException(AuthErrorCode.UNAUTHORIZED, e);
			} else if (status == HttpStatus.FORBIDDEN) {
				throw new KeystoneException(AuthErrorCode.FORBIDDEN_ACCESS, e);
			} else if (status == HttpStatus.NOT_FOUND) {
				throw new KeystoneException(AuthErrorCode.USER_NOT_FOUND, e);
			} else if (status == HttpStatus.CONFLICT) {
				throw new KeystoneException(AuthErrorCode.CONFLICT, e);
			}
			throw new KeystoneException(AuthErrorCode.KEYSTONE_API_FAILURE, e);
		}
	}

	@Override
	public UserListResponse listUsers(String token, String marker, Integer limit) {
		try {
			ResponseEntity<JsonNode> response = keystoneUserAPIModule.listUsers(token, marker, limit);
			return KeystoneAPIUtils.parseKeystoneUserListResponse(response);
		} catch (WebClientResponseException e) {
			HttpStatusCode status = e.getStatusCode();
			if (status == HttpStatus.UNAUTHORIZED) {
				throw new KeystoneException(AuthErrorCode.UNAUTHORIZED, e);
			} else if (status == HttpStatus.FORBIDDEN) {
				throw new KeystoneException(AuthErrorCode.FORBIDDEN_ACCESS, e);
			} else if (status == HttpStatus.BAD_REQUEST) {
				throw new KeystoneException(AuthErrorCode.INVALID_REQUEST_PARAMETER, e);
			}
			throw new KeystoneException(AuthErrorCode.KEYSTONE_API_FAILURE, e);
		}
	}

	// ----- Project -----

	@Override
	public KeystoneProject createProject(String adminToken, CreateKeystoneProjectRequest createKeystoneProjectRequest) {
		try {
			ResponseEntity<JsonNode> projectSavedResponse = keystoneProjectAPIModule.createProject(
					adminToken,
					createKeystoneProjectRequest.toKeystoneRequest()
			);

			if (projectSavedResponse == null) {
				throw new AuthServiceException(AuthErrorCode.KEYSTONE_PROJECT_CREATION_FAILED, "프로젝트 생성 응답이 null입니다.");
			}

			return KeystoneAPIUtils.parseKeystoneProjectResponse(projectSavedResponse);
		} catch (WebClientResponseException e) {
			HttpStatusCode status = e.getStatusCode();
			if (status == HttpStatus.UNAUTHORIZED) {
				throw new KeystoneException(AuthErrorCode.UNAUTHORIZED, e);
			} else if (status == HttpStatus.FORBIDDEN) {
				throw new KeystoneException(AuthErrorCode.FORBIDDEN_ACCESS, e);
			} else if (status == HttpStatus.BAD_REQUEST) {
				throw new KeystoneException(AuthErrorCode.INVALID_REQUEST_PARAMETER, e);
			} else if (status == HttpStatus.CONFLICT) {
				throw new KeystoneException(AuthErrorCode.CONFLICT, e);
			}
			throw new KeystoneException(AuthErrorCode.KEYSTONE_PROJECT_CREATION_FAILED, e);
		} catch (Exception e) {
			return null;
		}
	}

	public String getAdminProjectId(String token) {
		try {
			ResponseEntity<JsonNode> allProjects = keystoneProjectAPIModule.getProjects(token, Collections.emptyMap());
			return KeystoneAPIUtils.extractAdminProjectId(allProjects);
		} catch (WebClientResponseException e) {
			HttpStatusCode status = e.getStatusCode();
			if (status == HttpStatus.UNAUTHORIZED) {
				throw new KeystoneException(AuthErrorCode.UNAUTHORIZED, e);
			} else if (status == HttpStatus.FORBIDDEN) {
				throw new KeystoneException(AuthErrorCode.FORBIDDEN_ACCESS, e);
			} else if (status == HttpStatus.BAD_REQUEST) {
				throw new KeystoneException(AuthErrorCode.INVALID_REQUEST_PARAMETER, e);
			} else if (status == HttpStatus.CONFLICT) {
				throw new KeystoneException(AuthErrorCode.CONFLICT, e);
			}
			throw new KeystoneException(AuthErrorCode.KEYSTONE_PROJECT_CREATION_FAILED, e);
		} catch (Exception e) {
			return null;
		}
	}

	@Override
	public ProjectListDto getProjectsByProjectName(String keyword, PageRequest pageRequest, String token) {
		try {
			Map<String, String> keystoneListProjectRequest = Collections.emptyMap();
			if (keyword != null) {
				keystoneListProjectRequest = Collections.singletonMap("name", keyword);
			}
			if (pageRequest != null) {
				keystoneListProjectRequest = KeystoneAPIUtils.createKeystoneListProjectRequest(
					keyword,
					pageRequest.getMarker(), pageRequest.getLimit()
				);
			}

			ResponseEntity<JsonNode> projectResponse = keystoneProjectAPIModule.getProjects(
				token,
				keystoneListProjectRequest
			);

			List<KeystoneProject> keystoneProjects = KeystoneAPIUtils.convertProjectResponse(projectResponse);
			OpenstackPagination projectsPagination = KeystoneAPIUtils.getPaginateInfo(projectResponse, null, false);

			return ProjectListDto.from(keystoneProjects, projectsPagination);
		} catch (WebClientResponseException e) {
			HttpStatusCode status = e.getStatusCode();
			if (status == HttpStatus.UNAUTHORIZED) {
				throw new KeystoneException(AuthErrorCode.UNAUTHORIZED, e);
			} else if (status == HttpStatus.FORBIDDEN) {
				throw new KeystoneException(AuthErrorCode.FORBIDDEN_ACCESS, e);
			} else if (status == HttpStatus.NOT_FOUND) {
				throw new KeystoneException(AuthErrorCode.PROJECT_NOT_FOUND, e);
			}
			e.printStackTrace();
			throw new KeystoneException(AuthErrorCode.KEYSTONE_PROJECT_RETRIEVAL_FAILED, e);
		}
	}

	@Override
	public ProjectListDto getUserProjectsByProjectName(String keyword, PageRequest pageRequest, String requestUserId, String token) {

		try {
			Map<String, String> keystoneListProjectRequest = Collections.emptyMap();
			if (pageRequest != null) KeystoneAPIUtils.createKeystoneListProjectRequest(
				keyword,
				pageRequest.getMarker(), pageRequest.getLimit()
			);

			ResponseEntity<JsonNode> projectResponse = keystoneProjectAPIModule.getProjectsUser(
				token,
				requestUserId,
				keystoneListProjectRequest
			);

			List<KeystoneProject> keystoneProjects = KeystoneAPIUtils.convertProjectResponse(projectResponse);
			OpenstackPagination projectsPagination = KeystoneAPIUtils.getPaginateInfo(projectResponse, null, false);

			return ProjectListDto.from(keystoneProjects, projectsPagination);
		} catch (WebClientResponseException e) {
			HttpStatusCode status = e.getStatusCode();
			if (status == HttpStatus.UNAUTHORIZED) {
				throw new KeystoneException(AuthErrorCode.UNAUTHORIZED, e);
			} else if (status == HttpStatus.FORBIDDEN) {
				throw new KeystoneException(AuthErrorCode.FORBIDDEN_ACCESS, e);
			} else if (status == HttpStatus.NOT_FOUND) {
				throw new KeystoneException(AuthErrorCode.PROJECT_NOT_FOUND, e);
			}
			e.printStackTrace();
			throw new KeystoneException(AuthErrorCode.KEYSTONE_PROJECT_RETRIEVAL_FAILED, e);
		}
	}

	@Override
	public String getProjectRole(ProjectRole role, String token) {
		try {
			ResponseEntity<JsonNode> response = keystoneUserAPIModule.getRoles(token);
			return Objects.requireNonNull(
				KeystoneAPIUtils.parseAndFindKeystoneRoleId(response, "admin")
			) // TODO: Openstack 내 API 호출에러 방지를 위한 admin권한 부여 (임시)
			.orElseThrow(() -> new WebClientResponseException(404, "Role Not Found", null, null, null));
		} catch (WebClientResponseException e) {
			HttpStatusCode status = e.getStatusCode();
			if (status == HttpStatus.UNAUTHORIZED) {
				throw new KeystoneException(AuthErrorCode.UNAUTHORIZED, e);
			} else if (status == HttpStatus.FORBIDDEN) {
				throw new KeystoneException(AuthErrorCode.FORBIDDEN_ACCESS, e);
			} else if (status == HttpStatus.BAD_REQUEST) {
				throw new KeystoneException(AuthErrorCode.INVALID_REQUEST_PARAMETER, e);
			}
			throw new KeystoneException(AuthErrorCode.KEYSTONE_API_FAILURE, e);
		}
	}

	@Override
	public void assignProjectRole(String userId, String projectId, String projectRoleKeystoneId, String token) {
		try {
			keystoneUserAPIModule.assignRole(token, userId, projectId, projectRoleKeystoneId);
		} catch (WebClientResponseException e) {
			HttpStatusCode status = e.getStatusCode();
			if (status == HttpStatus.UNAUTHORIZED) {
				throw new KeystoneException(AuthErrorCode.UNAUTHORIZED, e);
			} else if (status == HttpStatus.FORBIDDEN) {
				throw new KeystoneException(AuthErrorCode.FORBIDDEN_ACCESS, e);
			} else if (status == HttpStatus.BAD_REQUEST) {
				throw new KeystoneException(AuthErrorCode.INVALID_REQUEST_PARAMETER, e);
			}
			throw new KeystoneException(AuthErrorCode.KEYSTONE_API_FAILURE, e);
		}
	}

	@Override
	public void retrieveProjectRole(String userId, String projectId, String projectRoleKeystoneId, String token) {
		try {
			keystoneUserAPIModule.deleteRole(token, userId, projectId, projectRoleKeystoneId);
		} catch (WebClientResponseException e) {
			HttpStatusCode status = e.getStatusCode();
			if (status == HttpStatus.UNAUTHORIZED) {
				throw new KeystoneException(AuthErrorCode.UNAUTHORIZED, e);
			} else if (status == HttpStatus.FORBIDDEN) {
				throw new KeystoneException(AuthErrorCode.FORBIDDEN_ACCESS, e);
			} else if (status == HttpStatus.BAD_REQUEST) {
				throw new KeystoneException(AuthErrorCode.INVALID_REQUEST_PARAMETER, e);
			}
			throw new KeystoneException(AuthErrorCode.KEYSTONE_API_FAILURE, e);
		}
	}

	@Deprecated
	@Override
	public List<UserKeystoneDto> getUsersByEmail(String keyword) {
		try {
			// keystoneUserAPIModule.deleteRole(token, userId, projectId, projectRoleKeystoneId);
			return null;
		} catch (WebClientResponseException e) {
			HttpStatusCode status = e.getStatusCode();
			if (status == HttpStatus.UNAUTHORIZED) {
				throw new KeystoneException(AuthErrorCode.UNAUTHORIZED, e);
			} else if (status == HttpStatus.FORBIDDEN) {
				throw new KeystoneException(AuthErrorCode.FORBIDDEN_ACCESS, e);
			} else if (status == HttpStatus.BAD_REQUEST) {
				throw new KeystoneException(AuthErrorCode.INVALID_REQUEST_PARAMETER, e);
			}
			throw new KeystoneException(AuthErrorCode.KEYSTONE_API_FAILURE, e);
		}
	}

	@Override
	public KeystoneProject getProjectDetail(String projectId, String token) {
		try {
			ResponseEntity<JsonNode> response = keystoneProjectAPIModule.getProjectDetail(projectId, token);

			if (response == null) {
				throw new AuthServiceException(AuthErrorCode.KEYSTONE_PROJECT_RETRIEVAL_FAILED, "프로젝트 조회 응답이 null입니다.");
			}

			return KeystoneAPIUtils.parseKeystoneProjectResponse(response);
		} catch (WebClientResponseException e) {
			HttpStatusCode status = e.getStatusCode();
			if (status == HttpStatus.UNAUTHORIZED) {
				throw new KeystoneException(AuthErrorCode.UNAUTHORIZED, e);
			} else if (status == HttpStatus.FORBIDDEN) {
				throw new KeystoneException(AuthErrorCode.FORBIDDEN_ACCESS, e);
			} else if (status == HttpStatus.NOT_FOUND) {
				throw new KeystoneException(AuthErrorCode.PROJECT_NOT_FOUND, e);
			}
			throw new KeystoneException(AuthErrorCode.KEYSTONE_PROJECT_RETRIEVAL_FAILED, e);
		}
	}

	@Override
	public KeystoneProject updateProject(String projectId, String token, UpdateKeystoneProjectRequest updateRequest) {
		try {
			ResponseEntity<JsonNode> keystoneProjectUpdateResponse = keystoneProjectAPIModule.updateProject(
					projectId, token,
					updateRequest.toKeystoneRequest()
			);

			if (keystoneProjectUpdateResponse == null) {
				throw new AuthServiceException(AuthErrorCode.KEYSTONE_PROJECT_UPDATE_FAILED, "프로젝트 업데이트 응답이 null입니다.");
			}

			return KeystoneAPIUtils.parseKeystoneProjectResponse(keystoneProjectUpdateResponse);
		} catch (WebClientResponseException e) {
			HttpStatusCode status = e.getStatusCode();
			if (status == HttpStatus.UNAUTHORIZED) {
				throw new KeystoneException(AuthErrorCode.UNAUTHORIZED, e);
			} else if (status == HttpStatus.FORBIDDEN) {
				throw new KeystoneException(AuthErrorCode.FORBIDDEN_ACCESS, e);
			} else if (status == HttpStatus.NOT_FOUND) {
				throw new KeystoneException(AuthErrorCode.PROJECT_NOT_FOUND, e);
			} else if (status == HttpStatus.BAD_REQUEST) {
				throw new KeystoneException(AuthErrorCode.INVALID_REQUEST_PARAMETER, e);
			} else if (status == HttpStatus.CONFLICT) {
				throw new KeystoneException(AuthErrorCode.CONFLICT, e);
			}
			throw new KeystoneException(AuthErrorCode.KEYSTONE_PROJECT_UPDATE_FAILED, e);
		}
	}

	@Override
	public void deleteProject(String projectId, String token) {
		try {
			keystoneProjectAPIModule.deleteProject(projectId, token);
			return;
		} catch (WebClientResponseException e) {
			HttpStatusCode status = e.getStatusCode();
			if (status == HttpStatus.UNAUTHORIZED) {
				throw new KeystoneException(AuthErrorCode.UNAUTHORIZED, e);
			} else if (status == HttpStatus.FORBIDDEN) {
				throw new KeystoneException(AuthErrorCode.FORBIDDEN_ACCESS, e);
			} else if (status == HttpStatus.NOT_FOUND) {
				throw new KeystoneException(AuthErrorCode.PROJECT_NOT_FOUND, e);
			} else if (status == HttpStatus.CONFLICT) {
				throw new KeystoneException(AuthErrorCode.CONFLICT, e);
			}
			throw new KeystoneException(AuthErrorCode.KEYSTONE_PROJECT_DELETION_FAILED, e);
		}
	}

	// ----- Role -----

	@Override
	public Map<String, ProjectRole> getAccountPermissionList(String userId, String token) {
		try {
			ResponseEntity<JsonNode> accountPermissionListResponse = keystoneRoleAPIModule.getAccountPermissionList(userId, token);
			if (accountPermissionListResponse == null) {
				throw new JwtAuthenticationException(AuthErrorCode.KEYSTONE_TOKEN_AUTHENTICATION_FAILED);
			}
			return KeystoneAPIUtils.createUserPermissionMap(accountPermissionListResponse);
		} catch (WebClientResponseException e) {
			HttpStatusCode status = e.getStatusCode();
			if (status == HttpStatus.UNAUTHORIZED) {
				throw new KeystoneException(AuthErrorCode.UNAUTHORIZED, e);
			} else if (status == HttpStatus.FORBIDDEN) {
				throw new KeystoneException(AuthErrorCode.FORBIDDEN_ACCESS, e);
			} else if (status == HttpStatus.NOT_FOUND) {
				throw new KeystoneException(AuthErrorCode.USER_NOT_FOUND, e);
			}
			throw new KeystoneException(AuthErrorCode.KEYSTONE_API_FAILURE, e);
		}
	}

	@Override
	public Role createRole(String token, Map<String, Object> roleRequest) {
		try {
			ResponseEntity<JsonNode> response = keystoneRoleAPIModule.createRole(token, roleRequest);
			return KeystoneAPIUtils.parseKeystoneRoleResponse(response);
		} catch (WebClientResponseException e) {
			HttpStatusCode status = e.getStatusCode();
			if (status == HttpStatus.UNAUTHORIZED) {
				throw new KeystoneException(AuthErrorCode.UNAUTHORIZED, e);
			} else if (status == HttpStatus.FORBIDDEN) {
				throw new KeystoneException(AuthErrorCode.FORBIDDEN_ACCESS, e);
			} else if (status == HttpStatus.BAD_REQUEST) {
				throw new KeystoneException(AuthErrorCode.INVALID_REQUEST_PARAMETER, e);
			} else if (status == HttpStatus.CONFLICT) {
				throw new KeystoneException(AuthErrorCode.CONFLICT, e);
			}
			throw new KeystoneException(AuthErrorCode.KEYSTONE_API_FAILURE, e);
		}
	}

	@Override
	public RoleListResponse listRoles(String token, String marker, Integer limit, String name) {
		try {
			ResponseEntity<JsonNode> response = keystoneRoleAPIModule.listRoles(token, marker, limit, name);
			return KeystoneAPIUtils.parseKeystoneRoleListResponse(response);
		} catch (WebClientResponseException e) {
			HttpStatusCode status = e.getStatusCode();
			if (status == HttpStatus.UNAUTHORIZED) {
				throw new KeystoneException(AuthErrorCode.UNAUTHORIZED, e);
			} else if (status == HttpStatus.FORBIDDEN) {
				throw new KeystoneException(AuthErrorCode.FORBIDDEN_ACCESS, e);
			} else if (status == HttpStatus.BAD_REQUEST) {
				throw new KeystoneException(AuthErrorCode.INVALID_REQUEST_PARAMETER, e);
			}
			throw new KeystoneException(AuthErrorCode.KEYSTONE_API_FAILURE, e);
		}
	}

	@Override
	public RoleAssignmentListResponse listRoleAssignments(String token, Map<String, String> filters) {
		try {
			ResponseEntity<JsonNode> response = keystoneRoleAPIModule.listRoleAssignments(token, filters);
			return KeystoneAPIUtils.parseKeystoneRoleAssignmentListResponse(response);
		} catch (WebClientResponseException e) {
			HttpStatusCode status = e.getStatusCode();
			if (status == HttpStatus.UNAUTHORIZED) {
				throw new KeystoneException(AuthErrorCode.UNAUTHORIZED, e);
			} else if (status == HttpStatus.FORBIDDEN) {
				throw new KeystoneException(AuthErrorCode.FORBIDDEN_ACCESS, e);
			} else if (status == HttpStatus.BAD_REQUEST) {
				throw new KeystoneException(AuthErrorCode.INVALID_REQUEST_PARAMETER, e);
			}
			throw new KeystoneException(AuthErrorCode.KEYSTONE_API_FAILURE, e);
		}
	}

	// ----- Private Methods -----

	private ResponseEntity<JsonNode> requestKeystoneTokenInfo(String keystoneToken) {
		try {
			ResponseEntity<JsonNode> tokenInfoResponse = keystoneAuthAPIModule.getTokenInfo(keystoneToken);
			if (tokenInfoResponse == null) {
				throw new KeystoneException(AuthErrorCode.KEYSTONE_TOKEN_AUTHENTICATION_FAILED);
			}
			return tokenInfoResponse;
		} catch (WebClientResponseException e) {
			HttpStatusCode status = e.getStatusCode();
			if (status == HttpStatus.UNAUTHORIZED) {
				throw new KeystoneException(AuthErrorCode.UNAUTHORIZED, e);
			} else if (status == HttpStatus.FORBIDDEN) {
				throw new KeystoneException(AuthErrorCode.FORBIDDEN_ACCESS, e);
			} else if (status == HttpStatus.NOT_FOUND) {
				throw new KeystoneException(AuthErrorCode.KEYSTONE_TOKEN_AUTHENTICATION_FAILED, e);
			}
			throw new KeystoneException(AuthErrorCode.KEYSTONE_API_FAILURE, e);
		}
	}

	private ResponseEntity<JsonNode> authenticateKeystoneByPassword(KeystonePasswordLoginRequest request) {
		try {
			Map<String, Object> passwordAuthRequest = KeystoneAPIUtils.createPasswordAuthRequest(request);
			ResponseEntity<JsonNode> loginResponse = keystoneAuthAPIModule.issueUnscopedToken(passwordAuthRequest);
			if (loginResponse == null) {
				throw new JwtAuthenticationException(AuthErrorCode.KEYSTONE_TOKEN_GENERATION_FAILED);
			}

			return loginResponse;
		} catch (WebClientResponseException e) {
			HttpStatusCode status = e.getStatusCode();
			if (status == HttpStatus.UNAUTHORIZED) {
				throw new KeystoneException(AuthErrorCode.UNAUTHORIZED, e);
			} else if (status == HttpStatus.FORBIDDEN) {
				throw new KeystoneException(AuthErrorCode.FORBIDDEN_ACCESS, e);
			} else if (status == HttpStatus.BAD_REQUEST) {
				throw new KeystoneException(AuthErrorCode.INVALID_REQUEST_PARAMETER, e);
			}
			throw new KeystoneException(AuthErrorCode.KEYSTONE_TOKEN_GENERATION_FAILED, e);
		}
	}

	private ResponseEntity<JsonNode> authenticateKeystoneByToken(String existingToken) {
		try {
			ResponseEntity<JsonNode> tokenResponse = keystoneAuthAPIModule.issueUnscopedTokenByToken(existingToken);
			if (tokenResponse == null) {
				throw new JwtAuthenticationException(AuthErrorCode.KEYSTONE_TOKEN_GENERATION_FAILED);
			}

			return tokenResponse;
		} catch (WebClientResponseException e) {
			HttpStatusCode status = e.getStatusCode();
			if (status == HttpStatus.UNAUTHORIZED) {
				throw new KeystoneException(AuthErrorCode.UNAUTHORIZED, e);
			} else if (status == HttpStatus.FORBIDDEN) {
				throw new KeystoneException(AuthErrorCode.FORBIDDEN_ACCESS, e);
			} else if (status == HttpStatus.BAD_REQUEST) {
				throw new KeystoneException(AuthErrorCode.INVALID_REQUEST_PARAMETER, e);
			}
			throw new KeystoneException(AuthErrorCode.KEYSTONE_TOKEN_GENERATION_FAILED, e);
		}
	}

	private ResponseEntity<JsonNode> authenticateKeystoneWithScope(String projectId, String unscopedTokenString) {

			Map<String, Object> tokenRequest = KeystoneAPIUtils.createProjectScopeTokenRequest(projectId , unscopedTokenString);
			ResponseEntity<JsonNode> tokenResponse = keystoneAuthAPIModule.issueScopedToken(tokenRequest);

			if (tokenResponse == null) {
				throw new KeystoneException(AuthErrorCode.KEYSTONE_TOKEN_EXTRACTION_FAILED, "프로젝트 스코프 토큰 발급 응답이 null입니다.");
			}

			return tokenResponse;

	}

	private ResponseEntity<JsonNode> authenticateKeystoneWithSystemPrivilege(String unscopedTokenString) {
		try {
			Map<String, Object> systemAdminTokenRequest = KeystoneAPIUtils.createSystemAdminTokenRequest(unscopedTokenString);
            log.trace("System Admin Token Request: {}", systemAdminTokenRequest);
			ResponseEntity<JsonNode> tokenResponse = keystoneAuthAPIModule.issueScopedToken(systemAdminTokenRequest);
			if (tokenResponse == null) {
				throw new KeystoneException(AuthErrorCode.KEYSTONE_TOKEN_EXTRACTION_FAILED, "프로젝트 스코프 토큰 발급 응답이 null입니다.");
			}
            log.trace("System Admin Token tokenResponse: {}", tokenResponse);
			return tokenResponse;
		} catch (WebClientResponseException e) {
			HttpStatusCode status = e.getStatusCode();
			if (status == HttpStatus.UNAUTHORIZED) {
				throw new KeystoneException(AuthErrorCode.UNAUTHORIZED, e);
			} else if (status == HttpStatus.FORBIDDEN) {
				throw new KeystoneException(AuthErrorCode.FORBIDDEN_ACCESS, e);
			} else if (status == HttpStatus.BAD_REQUEST) {
				throw new KeystoneException(AuthErrorCode.INVALID_REQUEST_PARAMETER, e);
			}
			throw new KeystoneException(AuthErrorCode.KEYSTONE_TOKEN_EXTRACTION_FAILED, e);
		}
	}

	private ResponseEntity<JsonNode> authenticateKeystoneWithAdminProjectPrivilege(String unscopedTokenString, String adminProjectId) {
		try {
			Map<String, Object> systemAdminTokenRequest = KeystoneAPIUtils.createSystemAdminProjectScopeTokenRequest(unscopedTokenString, adminProjectId);
            log.trace("System Admin Token Request: {}", systemAdminTokenRequest);
			ResponseEntity<JsonNode> tokenResponse = keystoneAuthAPIModule.issueScopedToken(systemAdminTokenRequest);
			if (tokenResponse == null) {
				throw new KeystoneException(AuthErrorCode.KEYSTONE_TOKEN_EXTRACTION_FAILED, "프로젝트 스코프 토큰 발급 응답이 null입니다.");
			}
            log.trace("System Admin Token tokenResponse: {}", tokenResponse);
			return tokenResponse;
		} catch (WebClientResponseException e) {
			HttpStatusCode status = e.getStatusCode();
			if (status == HttpStatus.UNAUTHORIZED) {
				throw new KeystoneException(AuthErrorCode.UNAUTHORIZED, e);
			} else if (status == HttpStatus.FORBIDDEN) {
				throw new KeystoneException(AuthErrorCode.FORBIDDEN_ACCESS, e);
			} else if (status == HttpStatus.BAD_REQUEST) {
				throw new KeystoneException(AuthErrorCode.INVALID_REQUEST_PARAMETER, e);
			}
			throw new KeystoneException(AuthErrorCode.KEYSTONE_TOKEN_EXTRACTION_FAILED, e);
		}
	}

}
