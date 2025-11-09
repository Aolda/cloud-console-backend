package com.acc.local.external.modules.keystone;

import com.acc.local.domain.enums.auth.KeystoneTokenType;
import com.acc.local.domain.enums.auth.ProjectPermission;
import com.acc.local.domain.model.auth.KeystoneProject;
import com.acc.local.domain.model.auth.User;
import com.acc.local.dto.auth.KeystoneToken;
import com.acc.local.dto.auth.KeystonePasswordLoginRequest;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.http.ResponseEntity;
import com.acc.global.exception.auth.AuthErrorCode;
import com.acc.global.exception.auth.KeystoneException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class KeystoneAPIUtils {
    //TODO : 테스트 베드 생성 후 정확한 포트 값 으로 지정 (keystonePropertise 에서 관리 ?)
    protected static final int port = 5000;

    public static KeystoneToken extractKeystoneToken(ResponseEntity<JsonNode> response) throws KeystoneException {
        JsonNode body = validateAndExtractBody(response);
        if (!body.has("token")) {
            throw new KeystoneException(
                AuthErrorCode.KEYSTONE_INVALID_RESPONSE_STRUCTURE,
                "Response body에 'token' 필드가 존재하지 않습니다."
            );
        }

        KeystoneTokenType tokenType = getKeystoneTokenType(body);
        try {
            return new KeystoneToken(
                tokenType,
                getAuditIds(body),
                getKeystoneTokenDate(body, "token.expires_at"),
                getKeystoneTokenDate(body, "token.issued_at"),
                getObjectValue(body, "token.user.id"),
                getObjectValue(body, "token.user.name"),
                getKeystoneTokenValue(response),
                isKeystoneTokenSystemRoleAdmin(body)
            );
        } catch (Exception e) {
            throw e;
            // throw new KeystoneException(
            //     AuthErrorCode.KEYSTONE_RESPONSE_PARSING_FAILED,
            //     "Token 정보 파싱 중 오류가 발생했습니다.",
            //     e
            // );
        }
    }

    private static boolean isKeystoneTokenSystemRoleAdmin(JsonNode body) {
        return Optional.ofNullable(getObjectValue(body, "token.system.all"))
            .orElse("false")
            .equals("true");
    }

    private static LocalDateTime getKeystoneTokenDate(JsonNode body, String dateKey) {
        return LocalDateTime.parse(getObjectValue(body, dateKey).toString().split("\\.")[0]);
    }

    private static KeystoneTokenType getKeystoneTokenType(JsonNode body) {
        KeystoneTokenType tokenType;
        Object projectFieldValue = getObjectValue(body, "token.project");
        if (projectFieldValue == null) tokenType = KeystoneTokenType.UNSCOPED;
        else tokenType = KeystoneTokenType.SCOPED;
        return tokenType;
    }

    private static String getKeystoneTokenValue(ResponseEntity<JsonNode> response) {
        String token = response.getHeaders().getFirst("X-Subject-Token");
        if (token == null || token.trim().isEmpty()) {
            throw new KeystoneException(
                AuthErrorCode.KEYSTONE_TOKEN_EXTRACTION_FAILED,
                "X-Subject-Token 헤더가 존재하지 않거나 비어있습니다."
            );
        }
        return token.trim();
    }

    private static List<String> getAuditIds(JsonNode body) {
        JsonNode auditIdsNode = body.get("token").get("audit_ids");
        List<String> auditIds = new ArrayList<>();
        if (auditIdsNode != null && auditIdsNode.isArray()) {
            for (JsonNode auditId : auditIdsNode) {
                auditIds.add(auditId.asText());
            }
        }
        return auditIds;
    }

    public static User parseKeystoneUserResponse(ResponseEntity<JsonNode> response) {
        JsonNode body = validateAndExtractBody(response);

        if (!body.has("user")) {
            throw new KeystoneException(
                AuthErrorCode.KEYSTONE_INVALID_RESPONSE_STRUCTURE,
                "Response body에 'user' 필드가 존재하지 않습니다."
            );
        }

        try {
            JsonNode userObject = body.get("user");
            //TODO : DTO 내부에 from 메서드로 정의
            return User.builder()
                    .id(userObject.has("id") ? userObject.get("id").asText() : null)
                    .name(userObject.has("name") ? userObject.get("name").asText() : null)
                    .domainId(userObject.has("domain_id") ? userObject.get("domain_id").asText() : null)
                    .defaultProjectId(userObject.has("default_project_id") ? userObject.get("default_project_id").asText() : null)
                    .enabled(userObject.has("enabled") ? userObject.get("enabled").asBoolean() : false)
                    .email(userObject.has("email") ? userObject.get("email").asText() : null)
                    .description(userObject.has("description") ? userObject.get("description").asText() : null)
                    .build();
        } catch (Exception e) {
            throw new KeystoneException(
                AuthErrorCode.KEYSTONE_RESPONSE_PARSING_FAILED,
                "User 정보 파싱 중 오류가 발생했습니다.",
                e
            );
        }
    }

    public static KeystoneProject parseKeystoneProjectResponse(ResponseEntity<JsonNode> response) {
        JsonNode body = validateAndExtractBody(response);

        if (!body.has("project")) {
            throw new KeystoneException(
                AuthErrorCode.KEYSTONE_INVALID_RESPONSE_STRUCTURE,
                "Response body에 'project' 필드가 존재하지 않습니다."
            );
        }

        try {
            JsonNode projectObject = body.get("project");

            //TODO : DTO 내부에 from 메서드로 정의
            return KeystoneProject.builder()
                    .id(projectObject.has("id") ? projectObject.get("id").asText() : null)
                    .name(projectObject.has("name") ? projectObject.get("name").asText() : null)
                    .description(projectObject.has("description") ? projectObject.get("description").asText() : null)
                    .domainId(projectObject.has("domain_id") ? projectObject.get("domain_id").asText() : null)
                    .enabled(projectObject.has("enabled") ? projectObject.get("enabled").asBoolean() : false)
                    .isDomain(projectObject.has("is_domain") ? projectObject.get("is_domain").asBoolean() : false)
                    .parentId(projectObject.has("parent_id") ? projectObject.get("parent_id").asText() : null)
                    .build();
        } catch (Exception e) {
            throw new KeystoneException(
                AuthErrorCode.KEYSTONE_RESPONSE_PARSING_FAILED,
                "Project 정보 파싱 중 오류가 발생했습니다.",
                e
            );
        }
    }

    public static Map<String, ProjectPermission> createUserPermissionMap(ResponseEntity<JsonNode> response) {
        JsonNode body = validateAndExtractBody(response);

        if (!body.has("role_assignments")) {
            throw new KeystoneException(
                AuthErrorCode.KEYSTONE_INVALID_RESPONSE_STRUCTURE,
                "Response body에 'role_assignments' 필드가 존재하지 않습니다."
            );
        }

        try {
            JsonNode roleAssignments = body.get("role_assignments");
            if (roleAssignments != null && roleAssignments.isArray()) {
                return StreamSupport.stream(roleAssignments.spliterator(), false)
                    .map(KeystoneAPIUtils::parseAssignedRoles)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toMap(
                        roleInfo -> roleInfo.get(0),
                        roleInfo -> ProjectPermission.findByKeystoneRoleName(roleInfo.get(1))
                    ));
            }
            return new HashMap<>();
        } catch (Exception e) {
            throw new KeystoneException(
                AuthErrorCode.KEYSTONE_RESPONSE_PARSING_FAILED,
                "Permission 맵 생성 중 오류가 발생했습니다.",
                e
            );
        }
    }

    // TODO: resquest, response 생성 및 파서 관리 유틸을 나눌 것인지 확인 필요
    // --  Request DTO 생성 메서드 ---//

    public static Map<String, Object> createKeystoneUserRequest(User user) {
        Map<String, Object> userObject = new HashMap<>();
        userObject.put("name", user.getName());
        userObject.put("password", user.getPassword());
        userObject.put("enabled", user.isEnabled());
        userObject.put("email", user.getEmail());

        Map<String, Object> request = new HashMap<>();
        request.put("user", userObject);

        return request;
    }

    public static Map<String, Object> createKeystoneUpdateUserRequest(User user) {
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

    public static Map<String, Object> createKeystoneProjectRequest(KeystoneProject project) {
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

    public static Map<String, Object> createKeystoneUpdateProjectRequest(KeystoneProject project) {
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

    public static Map<String, Object> createProjectScopeTokenRequest(String projectId, String unScopedToken) {
        Map<String, Object> authRequest = new HashMap<>();
        authRequest.put("auth", Map.of(
                "identity", Map.of(
                        "methods", List.of("token"),
                        "token", Map.of(
                                "id", unScopedToken // password 대신 token 사용
                        )
                ),
                "scope", Map.of("project", Map.of(
                        "id", projectId
                ))
        ));
        return authRequest;
    }

    public static Map<String, Object> createSystemAdminTokenRequest(String unScopedToken) {
        Map<String, Object> authRequest = new HashMap<>();
        authRequest.put("auth", Map.of(
            "identity", Map.of(
                "methods", List.of("token"),
                "token", Map.of(
                    "id", unScopedToken // password 대신 token 사용
                )
            ),
            "scope", Map.of("system", Map.of(
                "all", true
            ))
        ));
        return authRequest;
    }

    public static Map<String, Object> createPasswordAuthRequest(KeystonePasswordLoginRequest request) {
        Map<String, Object> authRequest = new HashMap<>();
        authRequest.put("auth", Map.of(
                "identity", Map.of(
                        "methods", List.of("password"),
                        "password", Map.of(
                                "user", Map.of(
                                        "name", request.username(),
                                        "domain", Map.of("name", request.domainName()),
                                        "password", request.password()
                                )
                        )
                )
        ));
        return authRequest;
    }

    public static Map<String, String> createKeystoneTokenInfoRequestHeader(String keystoneToken) {
        return generateKeystoneTokenTaskHeader(keystoneToken);
    }

    public static Map<String, String> createRevokeTokenRequestHeader(String revokeToken) {
        return generateKeystoneTokenTaskHeader(revokeToken);
    }

    protected static Map<String, String> generateKeystoneTokenTaskHeader(String keystoneToken) {
        Map<String, String> tokenTaskHeader = new HashMap<>();
        tokenTaskHeader.put("X-Auth-Token", keystoneToken);
        tokenTaskHeader.put("X-Subject-Token", keystoneToken);
        return tokenTaskHeader;
    }

    // -- Helper 메서드 분리 --//

    private static JsonNode validateAndExtractBody(ResponseEntity<JsonNode> response) {
        JsonNode body = response.getBody();
        if (body == null) {
            throw new KeystoneException(
                AuthErrorCode.KEYSTONE_INVALID_RESPONSE_STRUCTURE,
                "Response body가 null입니다."
            );
        }
        return body;
    }

    private static List<String> parseAssignedRoles(JsonNode assignedRole) {
        try {
            String projectId = getObjectValue(assignedRole, "scope.project.id");
            String keystoneRoleName = getObjectValue(assignedRole, "role.name");

            return Arrays.asList(projectId, keystoneRoleName);
        } catch (Exception e) {
            return null;
        }
    }

    private static <T> T getObjectValue(JsonNode object, String keys) {
        if (object == null) return null;

        String[] keyArray = keys.split("\\.");
        JsonNode current = object;

        for (String key : keyArray) {
            if (current == null || !current.has(key)) return null;
            current = current.get(key);
        }

        return (T) (current != null ? current.asText() : null);
    }

}
