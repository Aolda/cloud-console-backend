package com.acc.local.external.modules.keystone;

import com.acc.local.external.modules.OpenstackAPICallModule;
import com.acc.local.external.modules.keystone.constant.KeystoneRoutes;
import com.acc.local.service.modules.auth.KeystoneModule;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Map;

/**
 *  KeystoneAuthAPIModule
 *  : 키스톤 인증 관련한 외부 요청 메서드 관리 클래스 ex) "누구인가?"
 */
@Component
@RequiredArgsConstructor
public class KeystoneAuthAPIModule extends KeystoneAPIUtils {

    public final OpenstackAPICallModule openstackAPICallModule;

    public ResponseEntity<JsonNode> requestFederateLogin(String keycloakCode) {
        return openstackAPICallModule.callPostAPI(KeystoneRoutes.TOKEN_AUTH_FEDERATE, Collections.singletonMap("Authorization", "Bearer " + keycloakCode), Collections.emptyMap(), port);
    }

    public ResponseEntity<JsonNode> getTokenInfo(String token) {
        return openstackAPICallModule.callGetAPI(KeystoneRoutes.TOKEN_AUTH_DEFAULT, Collections.singletonMap("X-Auth-Token", token), Collections.emptyMap() ,port);
    }

    public ResponseEntity<JsonNode> getScopeTokenInfo(String token, Map<String, Object> request) {
        return openstackAPICallModule.callGetAPI(KeystoneRoutes.TOKEN_AUTH_DEFAULT, Collections.singletonMap("X-Auth-Token", token), Collections.emptyMap() , port);
    }

    public ResponseEntity<JsonNode> issueProjectScopeToken(Map<String, Object> tokenRequest) {
        return openstackAPICallModule.callPostAPI(KeystoneRoutes.TOKEN_AUTH_DEFAULT, Collections.emptyMap(), tokenRequest, port);
    }

    public ResponseEntity<JsonNode> issueUnscopedToken(Map<String, Object> passwordAuthRequest) {
        return openstackAPICallModule.callPostAPI(KeystoneRoutes.TOKEN_AUTH_DEFAULT, Collections.emptyMap(), passwordAuthRequest, port);
    }
}
