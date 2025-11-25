package com.acc.local.external.modules.keystone;

import com.acc.local.external.modules.OpenstackAPICallModule;
import com.acc.local.external.modules.keystone.constant.KeystoneRoutes;
import com.fasterxml.jackson.databind.JsonNode;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class KeystoneUserAPIModule {

    public final OpenstackAPICallModule openstackAPICallModule;

    public ResponseEntity<JsonNode> createUser(String token, Map<String, Object> userRequest) {
        return openstackAPICallModule.callPostAPI(KeystoneRoutes.CREATE_USER, Collections.singletonMap("X-Auth-Token", token), userRequest , KeystoneAPIUtils.port);
    }

    public ResponseEntity<JsonNode> getUserDetail(String userId, String token) {
        String uri = KeystoneRoutes.POST_USER.replace("{user_id}", userId);
        return openstackAPICallModule.callGetAPI(uri, Collections.singletonMap("X-Auth-Token", token), Collections.emptyMap(), KeystoneAPIUtils.port);
    }

    public ResponseEntity<JsonNode> updateUser(String userId, String token, Map<String, Object> userRequest) {
        String uri = KeystoneRoutes.POST_USER.replace("{user_id}", userId);
        return openstackAPICallModule.callPatchAPI(uri, Collections.singletonMap("X-Auth-Token", token), userRequest , KeystoneAPIUtils.port);
    }

    public ResponseEntity<JsonNode> deleteUser(String userId, String token) {
        String uri = KeystoneRoutes.POST_USER.replace("{user_id}", userId);
        return openstackAPICallModule.callDeleteAPI(uri, Collections.singletonMap("X-Auth-Token", token) , KeystoneAPIUtils.port);
    }

    public ResponseEntity<JsonNode> listUsers(String token, String marker, Integer limit) {
        Map<String, String> queryParams = new java.util.HashMap<>();
        if (marker != null && !marker.isEmpty()) {
            queryParams.put("marker", marker);
        }
        if (limit != null) {
            queryParams.put("limit", String.valueOf(limit));
        }
        return openstackAPICallModule.callGetAPI(
                KeystoneRoutes.CREATE_USER,
                Collections.singletonMap("X-Auth-Token", token),
                queryParams,
                KeystoneAPIUtils.port
        );
    }

    public ResponseEntity<JsonNode> listUsers(String token, String marker, Integer limit, String keyword) {
        Map<String, String> queryParams = new java.util.HashMap<>();
        if (marker != null && !marker.isEmpty()) {
            queryParams.put("marker", marker);
        }
        if (limit != null) {
            queryParams.put("limit", String.valueOf(limit));
        }
        if (keyword != null && !keyword.isEmpty()) {
            queryParams.put("name", keyword);
        }
        return openstackAPICallModule.callGetAPI(
                KeystoneRoutes.CREATE_USER,
                Collections.singletonMap("X-Auth-Token", token),
                queryParams,
                KeystoneAPIUtils.port
        );
    }

    public ResponseEntity<JsonNode> getRoles(String token) {
        return openstackAPICallModule.callGetAPI(KeystoneRoutes.GET_ROLES, Collections.singletonMap("X-Auth-Token", token), Collections.emptyMap(), KeystoneAPIUtils.port);
    }

    public ResponseEntity<JsonNode> assignRole(String token, String userId, String projectId, String projectRoleKeystoneId) {
        String uri = KeystoneRoutes.SET_ROLE
            .replace("{project_id}", projectId)
            .replace("{user_id}", userId)
            .replace("{role_id}", projectRoleKeystoneId)
            ;
        return openstackAPICallModule.callPutAPI(uri, Collections.singletonMap("X-Auth-Token", token), Collections.emptyMap(), KeystoneAPIUtils.port);
    }

    public ResponseEntity<JsonNode> deleteRole(String token, String userId, String projectId, String projectRoleKeystoneId) {
        String uri = KeystoneRoutes.SET_ROLE
            .replace("{project_id}", projectId)
            .replace("{user_id}", userId)
            .replace("{role_id}", projectRoleKeystoneId)
            ;
        return openstackAPICallModule.callDeleteAPI(uri, Collections.singletonMap("X-Auth-Token", token), KeystoneAPIUtils.port);
    }
}
