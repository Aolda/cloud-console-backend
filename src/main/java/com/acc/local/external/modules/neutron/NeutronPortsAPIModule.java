package com.acc.local.external.modules.neutron;

import com.acc.local.external.dto.neutron.ports.BulkCreatePortRequest;
import com.acc.local.external.dto.neutron.ports.CreatePortBindingRequest;
import com.acc.local.external.dto.neutron.ports.CreatePortRequest;
import com.acc.local.external.dto.neutron.ports.UpdatePortRequest;
import com.acc.local.external.modules.OpenstackAPICallModule;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;

import java.util.Collections;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class NeutronPortsAPIModule extends NeutronAPIUtil {

    private final OpenstackAPICallModule openstackAPICallModule;

    public ResponseEntity<JsonNode> listPorts(String token, Map<String, String> queryParams) {
        String uri = "/v2.0/ports";
        return openstackAPICallModule.callGetAPI(uri, Collections.singletonMap("X-Auth-Token", token), queryParams, port);
    }

    public ResponseEntity<JsonNode> listPorts(String token, MultiValueMap<String, String> queryParams) {
        String uri = "/v2.0/ports";
        return openstackAPICallModule.callGetAPI(uri, Collections.singletonMap("X-Auth-Token", token), queryParams, port);
    }

    public ResponseEntity<JsonNode> createPort(String token, CreatePortRequest request) {
        String uri = "/v2.0/ports";
        return openstackAPICallModule.callPostAPI(uri, Collections.singletonMap("X-Auth-Token", token), request, port);
    }

    public ResponseEntity<JsonNode> bulkCreatePorts(String token, BulkCreatePortRequest request) {
        String uri = "/v2.0/ports";
        return openstackAPICallModule.callPostAPI(uri, Collections.singletonMap("X-Auth-Token", token), request, port);
    }

    public ResponseEntity<JsonNode> showPort(String token, String portId) {
        String uri = "/v2.0/ports/" + portId;
        return openstackAPICallModule.callGetAPI(uri, Collections.singletonMap("X-Auth-Token", token), Collections.emptyMap(), port);
    }

    public ResponseEntity<JsonNode> updatePort(String token, String portId, UpdatePortRequest request) {
        String uri = "/v2.0/ports/" + portId;
        return openstackAPICallModule.callPutAPI(uri, Collections.singletonMap("X-Auth-Token", token), request, port);
    }

    public ResponseEntity<JsonNode> deletePort(String token, String portId) {
        String uri = "/v2.0/ports/" + portId;
        return openstackAPICallModule.callDeleteAPI(uri, Collections.singletonMap("X-Auth-Token", token), port);
    }

    public ResponseEntity<JsonNode> showPortBinding(String token, String portId) {
        String uri = "/v2.0/ports/" + portId + "/bindings";
        return openstackAPICallModule.callGetAPI(uri, Collections.singletonMap("X-Auth-Token", token), Collections.emptyMap(), port);
    }

    public ResponseEntity<JsonNode> createPortBinding(String token, String portId, CreatePortBindingRequest request) {
        String uri = "/v2.0/ports/" + portId + "/bindings";
        return openstackAPICallModule.callPostAPI(uri, Collections.singletonMap("X-Auth-Token", token), request, port);
    }

    public ResponseEntity<JsonNode> activatePortBinding(String token, String portId, String host) {
        String uri = "/v2.0/ports/" + portId + "/bindings/" + host + "/activate";
        return openstackAPICallModule.callPutAPI(uri, Collections.singletonMap("X-Auth-Token", token), null, port);
    }

    public ResponseEntity<JsonNode> deletePortBinding(String token, String portId, String host) {
        String uri = "/v2.0/ports/" + portId + "/bindings/" + host;
        return openstackAPICallModule.callDeleteAPI(uri, Collections.singletonMap("X-Auth-Token", token), port);
    }
}