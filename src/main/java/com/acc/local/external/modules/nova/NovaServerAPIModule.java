package com.acc.local.external.modules.nova;

import com.acc.local.external.dto.nova.server.CreateServerRequest;
import com.acc.local.external.dto.nova.server.UpdateServerRequest;
import com.acc.local.external.modules.OpenstackAPICallModule;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class NovaServerAPIModule extends NovaAPIUtil
{
    private final OpenstackAPICallModule openstackAPICallModule;

    public ResponseEntity<JsonNode> updateServer(String token, String serverId, UpdateServerRequest request) {
        String uri = "/v2.1/servers/" + serverId;
        return openstackAPICallModule.callPutAPI(uri, Collections.singletonMap("X-Auth-Token", token), request, port);
    }

    // Delete Server
    public ResponseEntity<JsonNode> deleteServer(String token, String serverId) {
        String uri = "/v2.1/servers/" + serverId;
        return openstackAPICallModule.callDeleteAPI(uri, Collections.singletonMap("X-Auth-Token", token), port);
    }

    public ResponseEntity<JsonNode> listServers(String token, Map<String, String> queryParams) {
        String uri = "/v2.1/servers";
        return openstackAPICallModule.callGetAPI(uri, Collections.singletonMap("X-Auth-Token", token), queryParams, port);
    }

    // List Servers without query parameters
    public ResponseEntity<JsonNode> listServers(String token) {
        return listServers(token, Collections.emptyMap());
    }

    // List Servers with Details (same as listServers but with more detailed response)
    public ResponseEntity<JsonNode> listServersDetail(String token, Map<String, String> queryParams) {
        String uri = "/v2.1/servers/detail";
        return openstackAPICallModule.callGetAPI(uri, Collections.singletonMap("X-Auth-Token", token), queryParams, port);
    }

    // List Servers with Details without query parameters
    public ResponseEntity<JsonNode> listServersDetail(String token) {
        return listServersDetail(token, Collections.emptyMap());
    }

    public ResponseEntity<JsonNode> showServer(String token, String serverId) {
        String uri = "/v2.1/servers/" + serverId;
        return openstackAPICallModule.callGetAPI(uri, Collections.singletonMap("X-Auth-Token", token), Collections.emptyMap(), port);
    }

    public ResponseEntity<JsonNode> createServer(String token, CreateServerRequest request) {
        String uri = "/v2.1/servers";
        return openstackAPICallModule.callPostAPI(uri, Collections.singletonMap("X-Auth-Token", token), request, port);
    }
}
