package com.acc.local.external.modules.nova;

import com.acc.local.external.dto.nova.portInterface.CreateInterfaceRequest;
import com.acc.local.external.modules.OpenstackAPICallModule;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.Collections;

@Component
@RequiredArgsConstructor
public class NovaPortInterfaceAPIModule extends NovaAPIUtil {

    private final OpenstackAPICallModule openstackAPICallModule;

    public ResponseEntity<JsonNode> createInterface(String token, String serverId, CreateInterfaceRequest request) {
        String uri = "/v2.1/servers/" + serverId + "/os-interface";
        return openstackAPICallModule.callPostAPI(uri, Collections.singletonMap("X-Auth-Token", token), request, port);
    }

    public ResponseEntity<JsonNode> listPortInterfaces(String token, String serverId) {
        String uri = "/v2.1/servers/" + serverId + "/os-interface";
        return openstackAPICallModule.callGetAPI(uri, Collections.singletonMap("X-Auth-Token", token), Collections.emptyMap(), port);
    }

    public ResponseEntity<JsonNode> showPortInterfaceDetails(String token, String serverId, String portId) {
        String uri = "/v2.1/servers/" + serverId + "/os-interface" + portId;
        return openstackAPICallModule.callGetAPI(uri, Collections.singletonMap("X-Auth-Token", token), Collections.emptyMap(), port);
    }

    public ResponseEntity<JsonNode> detachInterface(String token, String serverId, String portId) {
        String uri = "/v2.1/servers/" + serverId + "/os-interface" + portId;
        return openstackAPICallModule.callDeleteAPI(uri, Collections.singletonMap("X-Auth-Token", token), port);
    }
}
