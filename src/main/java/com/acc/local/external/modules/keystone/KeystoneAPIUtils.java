package com.acc.local.external.modules.keystone;

import com.acc.local.domain.enums.auth.KeystoneTokenType;
import com.acc.local.domain.enums.project.ProjectRole;
import com.acc.local.external.dto.OpenstackPagination;
import com.acc.local.external.dto.keystone.CreateKeystoneProjectRequest;
import com.acc.local.external.dto.keystone.KeystoneProject;
import com.acc.local.domain.model.auth.KeystoneUser;
import com.acc.local.domain.model.auth.*;
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
            throw new KeystoneException(
                AuthErrorCode.KEYSTONE_RESPONSE_PARSING_FAILED,
                "Token 정보 파싱 중 오류가 발생했습니다.",
                e
            );
        }
    }

    private static boolean isKeystoneTokenSystemRoleAdmin(JsonNode body) {
        boolean isTokenSystemScopeToken = Optional.ofNullable(getObjectValue(body, "token.system.all"))
            .orElse("false")
            .equals("true");
        boolean isTokenAdminProjectScopeToken = Optional.ofNullable(getObjectValue(body, "token.user.name"))
            .orElse("")
            .equals("admin");
        return isTokenSystemScopeToken || isTokenAdminProjectScopeToken;
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

    public static KeystoneUser parseKeystoneUserResponse(ResponseEntity<JsonNode> response) {
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
            return KeystoneUser.builder()
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

    public static Map<String, ProjectRole> createUserPermissionMap(ResponseEntity<JsonNode> response) {
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
                        roleInfo -> ProjectRole.findByKeystoneRoleName(roleInfo.get(1))
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

    public static Role parseKeystoneRoleResponse(ResponseEntity<JsonNode> response) {
        JsonNode body = validateAndExtractBody(response);

        if (!body.has("role")) {
            throw new KeystoneException(
                AuthErrorCode.KEYSTONE_INVALID_RESPONSE_STRUCTURE,
                "Response body에 'role' 필드가 존재하지 않습니다."
            );
        }

        try {
            JsonNode roleObject = body.get("role");
            return Role.builder()
                    .id(roleObject.has("id") ? roleObject.get("id").asText() : null)
                    .name(roleObject.has("name") ? roleObject.get("name").asText() : null)
                    .description(roleObject.has("description") ? roleObject.get("description").asText(null) : null)
                    .domainId(roleObject.has("domain_id") ? roleObject.get("domain_id").asText(null) : null)
                    .build();
        } catch (Exception e) {
            throw new KeystoneException(
                AuthErrorCode.KEYSTONE_RESPONSE_PARSING_FAILED,
                "Role 정보 파싱 중 오류가 발생했습니다.",
                e
            );
        }
    }

    public static RoleListResponse parseKeystoneRoleListResponse(ResponseEntity<JsonNode> response) {
        JsonNode body = validateAndExtractBody(response);

        if (!body.has("roles")) {
            throw new KeystoneException(
                AuthErrorCode.KEYSTONE_INVALID_RESPONSE_STRUCTURE,
                "Response body에 'roles' 필드가 존재하지 않습니다."
            );
        }

        try {
            JsonNode rolesNode = body.get("roles");
            JsonNode linksNode = body.get("links");

            List<Role> roles = new ArrayList<>();
            if (rolesNode != null && rolesNode.isArray()) {
                for (JsonNode roleNode : rolesNode) {
                    Role role = Role.builder()
                            .id(roleNode.has("id") ? roleNode.get("id").asText() : null)
                            .name(roleNode.has("name") ? roleNode.get("name").asText() : null)
                            .description(roleNode.has("description") ? roleNode.get("description").asText(null) : null)
                            .domainId(roleNode.has("domain_id") ? roleNode.get("domain_id").asText(null) : null)
                            .build();
                    roles.add(role);
                }
            }

            String nextMarker = extractMarkerFromLink(linksNode, "next");
            String prevMarker = extractMarkerFromLink(linksNode, "previous");

            return RoleListResponse.builder()
                    .roles(roles)
                    .nextMarker(nextMarker)
                    .prevMarker(prevMarker)
                    .build();
        } catch (Exception e) {
            throw new KeystoneException(
                AuthErrorCode.KEYSTONE_RESPONSE_PARSING_FAILED,
                "Role 목록 파싱 중 오류가 발생했습니다.",
                e
            );
        }
    }

    public static UserListResponse parseKeystoneUserListResponse(ResponseEntity<JsonNode> response) {
        JsonNode body = validateAndExtractBody(response);

        if (!body.has("users")) {
            throw new KeystoneException(
                AuthErrorCode.KEYSTONE_INVALID_RESPONSE_STRUCTURE,
                "Response body에 'users' 필드가 존재하지 않습니다."
            );
        }

        try {
            JsonNode usersNode = body.get("users");
            JsonNode linksNode = body.get("links");

            List<KeystoneUser> keystoneUsers = new ArrayList<>();
            if (usersNode != null && usersNode.isArray()) {
                for (JsonNode userNode : usersNode) {
                    KeystoneUser keystoneUser = KeystoneUser.builder()
                            .id(userNode.has("id") ? userNode.get("id").asText() : null)
                            .name(userNode.has("name") ? userNode.get("name").asText() : null)
                            .domainId(userNode.has("domain_id") ? userNode.get("domain_id").asText() : null)
                            .defaultProjectId(userNode.has("default_project_id") ? userNode.get("default_project_id").asText() : null)
                            .enabled(userNode.has("enabled") ? userNode.get("enabled").asBoolean() : false)
                            .email(userNode.has("email") ? userNode.get("email").asText() : null)
                            .description(userNode.has("description") ? userNode.get("description").asText() : null)
                            .build();
                    keystoneUsers.add(keystoneUser);
                }
            }

            String nextMarker = extractMarkerFromLink(linksNode, "next");
            String prevMarker = extractMarkerFromLink(linksNode, "previous");

            return UserListResponse.builder()
                    .keystoneUsers(keystoneUsers)
                    .nextMarker(nextMarker)
                    .prevMarker(prevMarker)
                    .build();
        } catch (Exception e) {
            throw new KeystoneException(
                AuthErrorCode.KEYSTONE_RESPONSE_PARSING_FAILED,
                "User 목록 파싱 중 오류가 발생했습니다.",
                e
            );
        }
    }

    // TODO: resquest, response 생성 및 파서 관리 유틸을 나눌 것인지 확인 필요

    // --  Request DTO 생성 메서드 ---//

    public static Map<String, Object> createKeystoneUserRequest(KeystoneUser keystoneUser) {
        Map<String, Object> userObject = new HashMap<>();
        userObject.put("name", keystoneUser.getName());
        userObject.put("password", keystoneUser.getPassword());
        userObject.put("enabled", keystoneUser.isEnabled());
        userObject.put("email", keystoneUser.getEmail());

        Map<String, Object> request = new HashMap<>();
        request.put("user", userObject);

        return request;
    }

    public static Map<String, Object> createKeystoneUpdateUserRequest(KeystoneUser keystoneUser) {
        Map<String, Object> userObject = new HashMap<>();

        if (keystoneUser.getName() != null) {
            userObject.put("name", keystoneUser.getName());
        }
        if (keystoneUser.getEmail() != null) {
            userObject.put("email", keystoneUser.getEmail());
        }
        if (keystoneUser.getPassword() != null) {
            userObject.put("password", keystoneUser.getPassword());
        }
        if (keystoneUser.getDescription() != null) {
            userObject.put("description", keystoneUser.getDescription());
        }
        if (keystoneUser.getDefaultProjectId() != null) {
            userObject.put("default_project_id", keystoneUser.getDefaultProjectId());
        }
        userObject.put("enabled", keystoneUser.isEnabled());

        Map<String, Object> request = new HashMap<>();
        request.put("user", userObject);

        return request;
    }

    public static Map<String, Object> createKeystoneCreateProjectRequest(CreateKeystoneProjectRequest project) {
        Map<String, Object> projectObject = new HashMap<>();
        projectObject.put("name", project.projectName());

        if (project.projectDescription() != null) {
            projectObject.put("description", project.projectDescription());
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

    public static Map<String, Object> createTokenAuthRequest(String existingToken) {
        Map<String, Object> authRequest = new HashMap<>();
        authRequest.put("auth", Map.of(
                "identity", Map.of(
                        "methods", List.of("token"),
                        "token", Map.of(
                                "id", existingToken
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

    public static Map<String, String> createKeystoneListProjectRequest(String projectName, String marker, int limit) {
        Map<String, String> request = new HashMap<>();
        request.put("name", projectName);
        request.put("marker", marker);
        request.put("limit", String.valueOf(limit));

        return request;
    }

    /**
     * Role Assignments API 요청 필터 생성
     * @param marker 페이지네이션 마커 (nullable)
     * @param limit 조회 개수 제한
     * @return Role Assignments 필터 맵
     */
    public static Map<String, String> createKeystoneRoleAssignmentsFilters(String marker, Integer limit) {
        Map<String, String> filters = new HashMap<>();
        filters.put("effective", ""); // 그룹 멤버십 포함
        filters.put("include_names", "true"); // 이름 정보 포함

        if (marker != null && !marker.isEmpty()) {
            filters.put("marker", marker);
        }
        if (limit != null) {
            filters.put("limit", String.valueOf(limit));
        }

        return filters;
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
            if (current == null) return null;
            if (current.isArray()) {
                current = current.path(Integer.parseInt(key));
                continue;
            }
            if (!current.has(key)) return null;

            current = current.get(key);
        }

        if (current == null) return null;
        if (current.isArray()) return (T) current;

        return (T) current.asText();

    }

    public static Map<String, Object> createSystemAdminProjectScopeTokenRequest(String unScopedToken, String adminProjectId) {
        Map<String, Object> authRequest = new HashMap<>();
        authRequest.put("auth", Map.of(
            "identity", Map.of(
                "methods", List.of("token"),
                "token", Map.of(
                    "id", unScopedToken // password 대신 token 사용
                )
            ),
            "scope", Map.of("project", Map.of(
                "id", adminProjectId
            ))
        ));
        return authRequest;
    }

    public static String extractAdminProjectId(ResponseEntity<JsonNode> response) {
        JsonNode body = validateAndExtractBody(response);
        if (!body.has("projects")) {
            throw new KeystoneException(
                AuthErrorCode.KEYSTONE_INVALID_RESPONSE_STRUCTURE,
                "Response body에 'projects' 필드가 존재하지 않습니다."
            );
        }

        JsonNode projects = getObjectValue(body, "projects");
        for (JsonNode project : projects) {
            if (project.get("name").asText().equals("admin")) {
                return project.get("id").asText();
            }
        }

        throw new KeystoneException(
            AuthErrorCode.FORBIDDEN_ACCESS,
            "생성된 토큰에 관리자 프로젝트에 대한 접근권한이 없습니다"
        );

    }

    public static List<KeystoneProject> convertProjectResponse(ResponseEntity<JsonNode> response) {
        JsonNode body = validateAndExtractBody(response);
        if (!body.has("projects")) {
            throw new KeystoneException(
                AuthErrorCode.KEYSTONE_INVALID_RESPONSE_STRUCTURE,
                "Response body에 'projects' 필드가 존재하지 않습니다."
            );
        }

        JsonNode projects = getObjectValue(body, "projects");
        List<KeystoneProject> responseProjects = new ArrayList<>();

        for (JsonNode project : projects) {
            responseProjects.add(
                KeystoneProject.builder()
                    .id(project.get("id").asText(""))
                    .name(project.get("name").asText(""))
                    .description(project.get("description").asText(""))
                    .isDomain(project.get("isDomain").asBoolean())
                    .enabled(project.get("enabled").asBoolean())
                    .parentId(project.get("parentId").asText(""))
                    .build()
            );
        }

        return responseProjects;
    }

    public static OpenstackPagination getPaginateInfo(ResponseEntity<JsonNode> response, String prevMarker, boolean isLast) {
        JsonNode body = validateAndExtractBody(response);
        if (!body.has("links")) {
            return null;
        }

        JsonNode projects = getObjectValue(body, "links");
        return OpenstackPagination.builder()
            .isFirst(projects.get("self").asText("").contains("marker"))
            .isLast(isLast)
            .nextMarker(projects.get("next").asText(""))
            .prevMarker(prevMarker)
            .build();
    }
    private static String extractMarkerFromLink(JsonNode linksNode, String linkType) {
        if (linksNode == null || !linksNode.has(linkType)) {
            return null;
        }

        String link = linksNode.get(linkType).asText(null);
        if (link == null || !link.contains("marker=")) {
            return null;
        }

        String marker = link.substring(link.indexOf("marker=") + 7);
        if (marker.contains("&")) {
            marker = marker.substring(0, marker.indexOf("&"));
        }

        return marker;
    }

    /**
     * Role Assignments API 응답 파싱
     */
    public static RoleAssignmentListResponse parseKeystoneRoleAssignmentListResponse(ResponseEntity<JsonNode> response) {
        JsonNode body = validateAndExtractBody(response);

        if (!body.has("role_assignments")) {
            throw new KeystoneException(
                AuthErrorCode.KEYSTONE_INVALID_RESPONSE_STRUCTURE,
                "Response body에 'role_assignments' 필드가 존재하지 않습니다."
            );
        }

        try {
            JsonNode assignmentsNode = body.get("role_assignments");
            JsonNode linksNode = body.get("links");

            List<RoleAssignment> roleAssignments = new ArrayList<>();
            if (assignmentsNode != null && assignmentsNode.isArray()) {
                for (JsonNode assignmentNode : assignmentsNode) {
                    RoleAssignment assignment = parseRoleAssignment(assignmentNode);
                    if (assignment != null) {
                        roleAssignments.add(assignment);
                    }
                }
            }

            String nextMarker = extractMarkerFromLink(linksNode, "next");
            String prevMarker = extractMarkerFromLink(linksNode, "previous");

            return RoleAssignmentListResponse.builder()
                    .roleAssignments(roleAssignments)
                    .nextMarker(nextMarker)
                    .prevMarker(prevMarker)
                    .build();
        } catch (Exception e) {
            throw new KeystoneException(
                AuthErrorCode.KEYSTONE_RESPONSE_PARSING_FAILED,
                "Role Assignment 목록 파싱 중 오류가 발생했습니다.",
                e
            );
        }
    }

    /**
     * 개별 Role Assignment 파싱
     */
    private static RoleAssignment parseRoleAssignment(JsonNode assignmentNode) {
        try {
            RoleAssignment.RoleAssignmentBuilder builder = RoleAssignment.builder();

            // Role 정보 파싱
            if (assignmentNode.has("role")) {
                JsonNode roleNode = assignmentNode.get("role");
                builder.role(RoleAssignment.RoleInfo.builder()
                        .id(roleNode.has("id") ? roleNode.get("id").asText() : null)
                        .name(roleNode.has("name") ? roleNode.get("name").asText() : null)
                        .build());
            }

            // User 정보 파싱
            if (assignmentNode.has("user")) {
                JsonNode userNode = assignmentNode.get("user");
                RoleAssignment.DomainInfo userDomain = null;
                if (userNode.has("domain")) {
                    JsonNode domainNode = userNode.get("domain");
                    userDomain = RoleAssignment.DomainInfo.builder()
                            .id(domainNode.has("id") ? domainNode.get("id").asText() : null)
                            .name(domainNode.has("name") ? domainNode.get("name").asText() : null)
                            .build();
                }
                builder.user(RoleAssignment.UserInfo.builder()
                        .id(userNode.has("id") ? userNode.get("id").asText() : null)
                        .name(userNode.has("name") ? userNode.get("name").asText() : null)
                        .domain(userDomain)
                        .build());
            }

            // Group 정보 파싱
            if (assignmentNode.has("group")) {
                JsonNode groupNode = assignmentNode.get("group");
                RoleAssignment.DomainInfo groupDomain = null;
                if (groupNode.has("domain")) {
                    JsonNode domainNode = groupNode.get("domain");
                    groupDomain = RoleAssignment.DomainInfo.builder()
                            .id(domainNode.has("id") ? domainNode.get("id").asText() : null)
                            .name(domainNode.has("name") ? domainNode.get("name").asText() : null)
                            .build();
                }
                builder.group(RoleAssignment.GroupInfo.builder()
                        .id(groupNode.has("id") ? groupNode.get("id").asText() : null)
                        .name(groupNode.has("name") ? groupNode.get("name").asText() : null)
                        .domain(groupDomain)
                        .build());
            }

            // Scope 정보 파싱
            if (assignmentNode.has("scope")) {
                JsonNode scopeNode = assignmentNode.get("scope");
                RoleAssignment.ScopeInfo.ScopeInfoBuilder scopeBuilder = RoleAssignment.ScopeInfo.builder();

                // System scope
                if (scopeNode.has("system")) {
                    JsonNode systemNode = scopeNode.get("system");
                    scopeBuilder.system(RoleAssignment.SystemInfo.builder()
                            .all(systemNode.has("all") ? systemNode.get("all").asBoolean() : false)
                            .build());
                }

                // Project scope
                if (scopeNode.has("project")) {
                    JsonNode projectNode = scopeNode.get("project");
                    RoleAssignment.DomainInfo projectDomain = null;
                    if (projectNode.has("domain")) {
                        JsonNode domainNode = projectNode.get("domain");
                        projectDomain = RoleAssignment.DomainInfo.builder()
                                .id(domainNode.has("id") ? domainNode.get("id").asText() : null)
                                .name(domainNode.has("name") ? domainNode.get("name").asText() : null)
                                .build();
                    }
                    scopeBuilder.project(RoleAssignment.ProjectInfo.builder()
                            .id(projectNode.has("id") ? projectNode.get("id").asText() : null)
                            .name(projectNode.has("name") ? projectNode.get("name").asText() : null)
                            .domain(projectDomain)
                            .build());
                }

                // Domain scope
                if (scopeNode.has("domain")) {
                    JsonNode domainNode = scopeNode.get("domain");
                    scopeBuilder.domain(RoleAssignment.DomainInfo.builder()
                            .id(domainNode.has("id") ? domainNode.get("id").asText() : null)
                            .name(domainNode.has("name") ? domainNode.get("name").asText() : null)
                            .build());
                }

                builder.scope(scopeBuilder.build());
            }

            // Links 정보 파싱
            if (assignmentNode.has("links")) {
                JsonNode linksNode = assignmentNode.get("links");
                Map<String, String> links = new HashMap<>();
                linksNode.fields().forEachRemaining(entry ->
                    links.put(entry.getKey(), entry.getValue().asText())
                );
                builder.links(links);
            }

            return builder.build();
        } catch (Exception e) {
            return null;
        }
    }

}
