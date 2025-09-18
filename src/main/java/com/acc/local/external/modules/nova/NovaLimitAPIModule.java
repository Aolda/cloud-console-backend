package com.acc.local.external.modules.nova;

import com.acc.local.external.modules.OpenstackAPICallModule;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class NovaLimitAPIModule extends NovaAPIUtil {

    private final OpenstackAPICallModule openstackAPICallModule;

    // Get Server Limits
    public ResponseEntity<JsonNode> getServerLimits(String token, Map<String, String> queryParams) {
        String uri = "/v2.1/limits";
        return openstackAPICallModule.callGetAPI(uri, Collections.singletonMap("X-Auth-Token", token), queryParams, port);
    }

    public ResponseEntity<JsonNode> getServerLimits(String token) {
        return getServerLimits(token, Collections.emptyMap());
    }
}
