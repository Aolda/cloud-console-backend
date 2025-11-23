package com.acc.local.external.ports;

import com.acc.global.common.PageRequest;
import com.acc.global.exception.AccBaseException;
import com.acc.local.dto.project.ProjectListDto;
import com.acc.local.domain.model.auth.Role;
import com.acc.local.domain.model.auth.RoleListResponse;
import com.acc.local.domain.model.auth.UserListResponse;
import com.acc.local.dto.auth.KeystonePasswordLoginRequest;
import com.acc.local.dto.auth.KeystoneToken;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.http.ResponseEntity;

import java.util.Map;

public interface KeystoneAPIExternalPort {

	// ----- Auth -----

	KeystoneToken getUnscopedToken(KeystonePasswordLoginRequest loginRequest) throws AccBaseException;

	KeystoneToken getUnscopedTokenByToken(String existingToken) throws AccBaseException;

	KeystoneToken getScopedToken(String projectId, String unscopedToken) throws AccBaseException;

	KeystoneToken getAdminToken(KeystonePasswordLoginRequest loginRequest) throws AccBaseException;

	KeystoneToken getAdminTokenWithAdminProjectScope(KeystonePasswordLoginRequest loginRequest) throws AccBaseException;

	void revokeToken(String keystoneToken) throws AccBaseException;

	KeystoneToken getTokenObject(String keystoneToken) throws AccBaseException;

	ResponseEntity<JsonNode> requestFederateLogin(String keycloakCode);

	ResponseEntity<JsonNode> getTokenInfo(String token);

	ResponseEntity<JsonNode> getScopeTokenInfo(String token, Map<String, Object> request);

	ResponseEntity<JsonNode> issueScopedToken(Map<String, Object> tokenRequest);

	ResponseEntity<JsonNode> issueUnscopedToken(Map<String, Object> passwordAuthRequest);

	// ----- User -----

	ResponseEntity<JsonNode> createUser(String token, Map<String, Object> userRequest);

	ResponseEntity<JsonNode> getUserDetail(String userId, String token);

	ResponseEntity<JsonNode> updateUser(String userId, String token, Map<String, Object> userRequest);

	ResponseEntity<JsonNode> deleteUser(String userId, String token);

	UserListResponse listUsers(String token, String marker, Integer limit);

	// ----- Project -----

	ResponseEntity<JsonNode> createProject(String token, Map<String, Object> projectRequest);

	ResponseEntity<JsonNode> getProjectDetail(String projectId, String token);

	ResponseEntity<JsonNode> updateProject(String projectId, String token, Map<String, Object> projectRequest);

	ResponseEntity<JsonNode> deleteProject(String projectId, String token);

	// ----- Role -----

	ResponseEntity<JsonNode> getAccountPermissionList(String userId, String token);

	Role createRole(String token, Map<String, Object> roleRequest);

	RoleListResponse listRoles(String token, String marker, Integer limit, String name);

	ProjectListDto getProjectsByProjectName(String keyword, PageRequest pageRequest, String adminToken);
}
