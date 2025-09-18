package com.acc.local.external.modules.nova;

import com.acc.local.external.dto.nova.serverConsole.CreateConsoleRequest;
import com.acc.local.external.modules.OpenstackAPICallModule;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.Collections;

@Component
@RequiredArgsConstructor
public class NovaServerConsoleAPIModule extends NovaAPIUtil
{
    private final OpenstackAPICallModule openstackAPICallModule;

    public ResponseEntity<JsonNode> createConsole(String token, String serverId, CreateConsoleRequest request) {
        String uri = "/v2.1/servers/" + serverId + "/remote-consoles";
        return openstackAPICallModule.callPostAPI(uri, Collections.singletonMap("X-Auth-Token", token), request, port);
    }

    public ResponseEntity<JsonNode> showConsoleConnectionInfo(String token, String consoleToken) {
        String uri = "/v2.1/os-console-auth-tokens/" + consoleToken;
        return openstackAPICallModule.callGetAPI(uri, Collections.singletonMap("X-Auth-Token", token), Collections.emptyMap(), port);
    }
}
