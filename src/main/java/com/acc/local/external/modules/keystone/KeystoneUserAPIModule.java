package com.acc.local.external.modules.keystone;

import com.acc.local.external.modules.OpenstackAPICallModule;
import com.acc.local.external.modules.keystone.constant.KeystoneRoutes;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
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
        return openstackAPICallModule.callPutAPI(uri, Collections.singletonMap("X-Auth-Token", token), userRequest , KeystoneAPIUtils.port);
    }

    public ResponseEntity<JsonNode> deleteUser(String userId, String token) {
        String uri = KeystoneRoutes.POST_USER.replace("{user_id}", userId);
        return openstackAPICallModule.callDeleteAPI(uri, Collections.singletonMap("X-Auth-Token", token) , KeystoneAPIUtils.port);
    }
}
