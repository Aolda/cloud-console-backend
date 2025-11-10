package com.acc.local.external.adapters.keystone;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import com.acc.global.exception.AccBaseException;
import com.acc.global.exception.auth.AuthErrorCode;
import com.acc.global.exception.auth.JwtAuthenticationException;
import com.acc.global.exception.auth.KeystoneException;
import com.acc.local.dto.auth.KeystonePasswordLoginRequest;
import com.acc.local.dto.auth.KeystoneToken;
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
		ResponseEntity<JsonNode> systemAdminTokenResponse = authenticateKeystoneWithSystemPrivilege(unscopedToken.token());
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
	public ResponseEntity<JsonNode> getTokenInfo(String token) {
		try {
			return keystoneAuthAPIModule.getTokenInfo(token);
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

	@Override
	public ResponseEntity<JsonNode> getScopeTokenInfo(String token, Map<String, Object> request) {
		try {
			return keystoneAuthAPIModule.getScopeTokenInfo(token, request);
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
	public ResponseEntity<JsonNode> issueScopedToken(Map<String, Object> tokenRequest) {
		try {
			return keystoneAuthAPIModule.issueScopedToken(tokenRequest);
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

	@Override
	public ResponseEntity<JsonNode> issueUnscopedToken(Map<String, Object> passwordAuthRequest) {
		try {
			return keystoneAuthAPIModule.issueUnscopedToken(passwordAuthRequest);
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

	// ----- User -----

	@Override
	public ResponseEntity<JsonNode> createUser(String token, Map<String, Object> userRequest) {
		try {
			return keystoneUserAPIModule.createUser(token, userRequest);
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
	public ResponseEntity<JsonNode> getUserDetail(String userId, String token) {
		try {
			return keystoneUserAPIModule.getUserDetail(userId, token);
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
	public ResponseEntity<JsonNode> updateUser(String userId, String token, Map<String, Object> userRequest) {
		try {
			return keystoneUserAPIModule.updateUser(userId, token, userRequest);
		} catch (WebClientResponseException e) {
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
	public ResponseEntity<JsonNode> deleteUser(String userId, String token) {
		try {
			return keystoneUserAPIModule.deleteUser(userId, token);
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

	// ----- Project -----

	@Override
	public ResponseEntity<JsonNode> createProject(String token, Map<String, Object> projectRequest) {
		try {
			return keystoneProjectAPIModule.createProject(token, projectRequest);
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
		}
	}

	@Override
	public ResponseEntity<JsonNode> getProjectDetail(String projectId, String token) {
		try {
			return keystoneProjectAPIModule.getProjectDetail(projectId, token);
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
	public ResponseEntity<JsonNode> updateProject(String projectId, String token, Map<String, Object> projectRequest) {
		try {
			return keystoneProjectAPIModule.updateProject(projectId, token, projectRequest);
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
	public ResponseEntity<JsonNode> deleteProject(String projectId, String token) {
		try {
			return keystoneProjectAPIModule.deleteProject(projectId, token);
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
	public ResponseEntity<JsonNode> getAccountPermissionList(String userId, String token) {
		try {
			return keystoneRoleAPIModule.getAccountPermissionList(userId, token);
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
		try {
			Map<String, Object> tokenRequest = KeystoneAPIUtils.createProjectScopeTokenRequest(projectId , unscopedTokenString);
			ResponseEntity<JsonNode> tokenResponse = keystoneAuthAPIModule.issueScopedToken(tokenRequest);
			if (tokenResponse == null) {
				throw new KeystoneException(AuthErrorCode.KEYSTONE_TOKEN_EXTRACTION_FAILED, "프로젝트 스코프 토큰 발급 응답이 null입니다.");
			}

			return tokenResponse;
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
			}
			throw new KeystoneException(AuthErrorCode.KEYSTONE_TOKEN_EXTRACTION_FAILED, e);
		}
	}

	private ResponseEntity<JsonNode> authenticateKeystoneWithSystemPrivilege(String unscopedTokenString) {
		try {
			Map<String, Object> systemAdminTokenRequest = KeystoneAPIUtils.createSystemAdminTokenRequest(unscopedTokenString);
			ResponseEntity<JsonNode> tokenResponse = keystoneAuthAPIModule.issueScopedToken(systemAdminTokenRequest);
			if (tokenResponse == null) {
				throw new KeystoneException(AuthErrorCode.KEYSTONE_TOKEN_EXTRACTION_FAILED, "프로젝트 스코프 토큰 발급 응답이 null입니다.");
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
			throw new KeystoneException(AuthErrorCode.KEYSTONE_TOKEN_EXTRACTION_FAILED, e);
		}
	}

}
