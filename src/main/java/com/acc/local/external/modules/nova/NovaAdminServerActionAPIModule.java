package com.acc.local.external.modules.nova;

import com.acc.local.external.dto.nova.adminServerAction.*;
import com.acc.local.external.modules.OpenstackAPICallModule;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.Collections;

@Component
@RequiredArgsConstructor
public class NovaAdminServerActionAPIModule extends NovaAPIUtil
{
    private final OpenstackAPICallModule openstackAPICallModule;

    public ResponseEntity<JsonNode> injectNetworkInfo(String token, String serverId, InjectNetworkInfoRequest request) {
        String uri = "/v2.1/servers/" + serverId + "/action";
        return openstackAPICallModule.callPostAPI(uri, Collections.singletonMap("X-Auth-Token", token), request, port);
    }

    public ResponseEntity<JsonNode> migrateServer(String token, String serverId, MigrateServerRequest request) {
        String uri = "/v2.1/servers/" + serverId + "/action";
        return openstackAPICallModule.callPostAPI(uri, Collections.singletonMap("X-Auth-Token", token), request, port);
    }

    public ResponseEntity<JsonNode> liveMigrateServer(String token, String serverId, LiveMigrateServerRequest request) {
        String uri = "/v2.1/servers/" + serverId + "/action";
        return openstackAPICallModule.callPostAPI(uri, Collections.singletonMap("X-Auth-Token", token), request, port);
    }

    public ResponseEntity<JsonNode> resetServerState(String token, String serverId, ResetServerStateRequest request) {
        String uri = "/v2.1/servers/" + serverId + "/action";
        return openstackAPICallModule.callPostAPI(uri, Collections.singletonMap("X-Auth-Token", token), request, port);
    }

    public ResponseEntity<JsonNode> evacuateServer(String token, String serverId, EvacuateServerRequest request) {
        String uri = "/v2.1/servers/" + serverId + "/action";
        return openstackAPICallModule.callPostAPI(uri, Collections.singletonMap("X-Auth-Token", token), request, port);
    }
}
