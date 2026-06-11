package com.acc.local.external.ports;

import com.acc.global.common.PageRequest;
import com.acc.global.exception.AccBaseException;
import com.acc.local.domain.enums.project.ProjectRole;
import com.acc.local.dto.auth.UserKeystoneDto;
import com.acc.local.domain.model.auth.RoleAssignmentListResponse;
import com.acc.local.dto.project.ProjectListDto;
import com.acc.local.domain.model.auth.Role;
import com.acc.local.domain.model.auth.RoleListResponse;
import com.acc.local.domain.model.auth.UserListResponse;
import com.acc.local.dto.auth.KeystonePasswordLoginRequest;
import com.acc.local.dto.auth.KeystoneToken;
import com.acc.local.external.dto.keystone.*;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

public interface KeystoneAPIExternalPort {

	// ----- Auth -----

	KeystoneToken getUnscopedToken(KeystonePasswordLoginRequest loginRequest) throws AccBaseException;

	KeystoneToken getUnscopedTokenByToken(String existingToken) throws AccBaseException;

	KeystoneToken getScopedToken(String projectId, String unscopedToken) throws AccBaseException;

	KeystoneToken getScopedTokenByPassword(String projectId, KeystonePasswordLoginRequest loginRequest) throws AccBaseException;

	KeystoneToken getAdminToken(KeystonePasswordLoginRequest loginRequest) throws AccBaseException;

	KeystoneToken getAdminTokenWithAdminProjectScope(KeystonePasswordLoginRequest loginRequest) throws AccBaseException;

	void revokeToken(String keystoneToken) throws AccBaseException;

	KeystoneToken getTokenObject(String keystoneToken) throws AccBaseException;

	ResponseEntity<JsonNode> requestFederateLogin(String keycloakCode);

	KeystoneToken getTokenInfo(String token);

	// ----- User -----

	UserKeystoneDto createUser(String token, CreateKeystoneUserRequest createUserRequest);

	UserKeystoneDto getUserDetail(String userId, String token);

	UserKeystoneDto updateUser(String userId, String token, UpdateKeystoneUserRequest userRequest);

	void deleteUser(String userId, String token);

	UserListResponse listUsers(String token, String marker, Integer limit);

	// ----- Project -----

	KeystoneProject createProject(String token, CreateKeystoneProjectRequest createKeystoneProjectRequest);

	KeystoneProject getProjectDetail(String projectId, String token);

	KeystoneProject updateProject(String projectId, String token, UpdateKeystoneProjectRequest updateRequest);

	void deleteProject(String projectId, String token);

	// ----- Role -----

	Map<String, ProjectRole> getAccountPermissionList(String userId, String token);

	Role createRole(String token, Map<String, Object> roleRequest);

	RoleListResponse listRoles(String token, String marker, Integer limit, String name);

	RoleAssignmentListResponse listRoleAssignments(String token, Map<String, String> filters);

	ProjectListDto getProjectsByProjectName(String keyword, PageRequest pageRequest, String adminToken);

	ProjectListDto getUserProjectsByProjectName(String keyword, PageRequest pageRequest, String requestUserId, String token);

	String getProjectRole(ProjectRole role, String token);

	void assignProjectRole(String userId, String projectId, String projectRoleKeystoneId, String token);

	void retrieveProjectRole(String userId, String projectId, String projectRole, String token);

	List<UserKeystoneDto> getUsersByEmail(String keyword);

	String getAdminProjectId(String token);
}
