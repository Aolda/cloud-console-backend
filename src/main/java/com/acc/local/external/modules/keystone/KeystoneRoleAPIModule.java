package com.acc.local.external.modules.keystone;


import com.acc.local.external.modules.OpenstackAPICallModule;
import com.acc.local.external.modules.keystone.constant.KeystoneRoutes;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * KeystoneRoleAPIModule
 * : 권한/역할 관련 로직 관리 클래스 ex) 무엇을 할 수 있는가?
 */
@Component
@RequiredArgsConstructor
public class KeystoneRoleAPIModule {

    public final OpenstackAPICallModule openstackAPICallModule;

    public ResponseEntity<JsonNode> getAccountPermissionList(String userId, String token) {
        String uri = KeystoneRoutes.GET_ASSIGNED_PERMISSIONS.replace("{user_id}", userId) + "?user.id=" + userId + "&effective&include_names=true";
        return openstackAPICallModule.callGetAPI(uri, Collections.singletonMap("X-Auth-Token", token), Collections.emptyMap(), KeystoneAPIUtils.port);
    }

    public ResponseEntity<JsonNode> createRole(String token, Map<String, Object> roleRequest) {
        return openstackAPICallModule.callPostAPI(KeystoneRoutes.CREATE_ROLE, Collections.singletonMap("X-Auth-Token", token), roleRequest, KeystoneAPIUtils.port);
    }

    public ResponseEntity<JsonNode> listRoles(String token, String marker, Integer limit, String name) {
        Map<String, String> queryParams = new HashMap<>();
        if (marker != null && !marker.isEmpty()) {
            queryParams.put("marker", marker);
        }
        if (limit != null) {
            queryParams.put("limit", String.valueOf(limit));
        }
        if (name != null && !name.isEmpty()) {
            queryParams.put("name", name);
        }
        return openstackAPICallModule.callGetAPI(
                KeystoneRoutes.LIST_ROLES,
                Collections.singletonMap("X-Auth-Token", token),
                queryParams,
                KeystoneAPIUtils.port
        );
    }

    /**
     * @param token 인증 토큰
     * @param filters 필터 옵션 Map
     *                - "effective": "true" (그룹 멤버십 포함)
     *                - "include_names": "true" (이름 정보 포함)
     *                - "scope.system": "all" (시스템 스코프)
     *                - "scope.project.id": "{project_id}" (프로젝트 스코프)
     *                - "user.id": "{user_id}" (특정 사용자)
     *                - "role.id": "{role_id}" (특정 역할)
     *                - "marker": "{marker}" (페이지네이션)
     *                - "limit": "{limit}" (개수 제한)
     * @return Role assignments 응답
     */
    public ResponseEntity<JsonNode> listRoleAssignments(String token, Map<String, String> filters) {
        Map<String, String> queryParams = new HashMap<>();

        if (filters != null && !filters.isEmpty()) {
            queryParams.putAll(filters);
        }

        return openstackAPICallModule.callGetAPI(
                KeystoneRoutes.ROLE_ASSIGNMENTS,
                Collections.singletonMap("X-Auth-Token", token),
                queryParams,
                KeystoneAPIUtils.port
        );
    }
}
