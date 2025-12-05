package com.acc.local.external.modules.keystone;

import com.acc.global.common.PageRequest;
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
public class KeystoneProjectAPIModule {

    public final OpenstackAPICallModule openstackAPICallModule;

    public ResponseEntity<JsonNode> createProject(String token, Map<String, Object> projectRequest) {
        return openstackAPICallModule.callPostAPI(KeystoneRoutes.PROJECT_DEFAULT, Collections.singletonMap("X-Auth-Token", token), projectRequest, KeystoneAPIUtils.port);
    }

    public ResponseEntity<JsonNode> getAccessableProjects(String anyToken) {
        return openstackAPICallModule.callGetAPI(KeystoneRoutes.PROJECT_AUTH_DEFAULT, Collections.singletonMap("X-Auth-Token", anyToken), Collections.emptyMap(), KeystoneAPIUtils.port);
    }

    public ResponseEntity<JsonNode> getProjectDetail(String projectId, String token) {
        String uri = KeystoneRoutes.POST_PROJECT.replace("{project_id}", projectId);
        return openstackAPICallModule.callGetAPI(uri, Collections.singletonMap("X-Auth-Token", token), Collections.emptyMap(), KeystoneAPIUtils.port);
    }

    public ResponseEntity<JsonNode> updateProject(String projectId, String token, Map<String, Object> projectRequest) {
        String uri = KeystoneRoutes.POST_PROJECT.replace("{project_id}", projectId);
        return openstackAPICallModule.callPatchAPI(uri, Collections.singletonMap("X-Auth-Token", token), projectRequest, KeystoneAPIUtils.port);
    }

    public ResponseEntity<JsonNode> deleteProject(String projectId, String token) {
        String uri = KeystoneRoutes.POST_PROJECT.replace("{project_id}", projectId);
        return openstackAPICallModule.callDeleteAPI(uri, Collections.singletonMap("X-Auth-Token", token), KeystoneAPIUtils.port);
    }

    public ResponseEntity<JsonNode> getProjects(String token, Map<String, String> projectListRequest) {
        return openstackAPICallModule.callGetAPI(KeystoneRoutes.PROJECT_DEFAULT, Collections.singletonMap("X-Auth-Token", token), projectListRequest, KeystoneAPIUtils.port);
    }

    public ResponseEntity<JsonNode> getProjectsUser(String token, String userId, Map<String, String> projectListRequest) {
        String uri = KeystoneRoutes.PROJECT_USER.replace("{user_id}", userId);
        return openstackAPICallModule.callGetAPI(uri, Collections.singletonMap("X-Auth-Token", token), projectListRequest, KeystoneAPIUtils.port);
    }
}
