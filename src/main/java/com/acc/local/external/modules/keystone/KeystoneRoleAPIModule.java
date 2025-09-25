package com.acc.local.external.modules.keystone;


import com.acc.local.external.modules.OpenstackAPICallModule;
import com.acc.local.external.modules.keystone.constant.KeystoneRoutes;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.Collections;

/**
 * KeystoneRoleAPIModule
 * : 권한/역할 관련 로직 관리 클래스 ex) 무엇을 할 수 있는가?
 */
@Component
@RequiredArgsConstructor
public class KeystoneRoleAPIModule extends KeystoneAPIUtils{

    public final OpenstackAPICallModule openstackAPICallModule;

    public ResponseEntity<JsonNode> getAccountPermissionList(String userId, String token) {
        String uri = KeystoneRoutes.GET_ASSIGNED_PERMISSIONS.replace("{user_id}", userId) + "?user.id=" + userId + "&effective&include_names=true";
        return openstackAPICallModule.callGetAPI(uri, Collections.singletonMap("X-Auth-Token", token), Collections.emptyMap() ,port);
    }
}
