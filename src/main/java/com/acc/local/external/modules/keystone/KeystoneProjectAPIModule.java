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
public class KeystoneProjectAPIModule extends KeystoneAPIUtils{

    public final OpenstackAPICallModule openstackAPICallModule;

    public ResponseEntity<JsonNode> createProject(String token, Map<String, Object> projectRequest) {
        return openstackAPICallModule.callPostAPI(KeystoneRoutes.CREATE_PROJECT, Collections.singletonMap("X-Auth-Token", token), projectRequest , port);
    }

    public ResponseEntity<JsonNode> getProjectDetail(String projectId, String token) {
        String uri = KeystoneRoutes.POST_PROJECT.replace("{project_id}", projectId);
        return openstackAPICallModule.callGetAPI(uri, Collections.singletonMap("X-Auth-Token", token), Collections.emptyMap() ,port);
    }

    public ResponseEntity<JsonNode> updateProject(String projectId, String token, Map<String, Object> projectRequest) {
        String uri = KeystoneRoutes.POST_PROJECT.replace("{project_id}", projectId);
        return openstackAPICallModule.callPatchAPI(uri, Collections.singletonMap("X-Auth-Token", token), projectRequest ,port);
    }

    public ResponseEntity<JsonNode> deleteProject(String projectId, String token) {
        String uri = KeystoneRoutes.POST_PROJECT.replace("{project_id}", projectId);
        return openstackAPICallModule.callDeleteAPI(uri, Collections.singletonMap("X-Auth-Token", token), port);
    }
}
