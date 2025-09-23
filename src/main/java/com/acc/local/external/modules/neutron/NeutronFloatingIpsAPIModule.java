package com.acc.local.external.modules.neutron;

import com.acc.local.external.dto.neutron.floatingips.CreateFloatingIpRequest;
import com.acc.local.external.dto.neutron.floatingips.UpdateFloatingIpRequest;
import com.acc.local.external.dto.neutron.portforwardings.CreatePortForwardingRequest;
import com.acc.local.external.dto.neutron.portforwardings.UpdatePortForwardingRequest;
import com.acc.local.external.modules.OpenstackAPICallModule;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class NeutronFloatingIpsAPIModule extends NeutronAPIUtil {

    private final OpenstackAPICallModule openstackAPICallModule;

    public ResponseEntity<JsonNode> listFloatingIps(String token, Map<String, String> queryParams) {
        String uri = "/v2.0/floatingips";
        return openstackAPICallModule.callGetAPI(uri, Collections.singletonMap("X-Auth-Token", token), queryParams, port);
    }

    public ResponseEntity<JsonNode> createFloatingIp(String token, CreateFloatingIpRequest request) {
        String uri = "/v2.0/floatingips";
        return openstackAPICallModule.callPostAPI(uri, Collections.singletonMap("X-Auth-Token", token), request, port);
    }

    public ResponseEntity<JsonNode> showFloatingIp(String token, String floatingIpId) {
        String uri = "/v2.0/floatingips/" + floatingIpId;
        return openstackAPICallModule.callGetAPI(uri, Collections.singletonMap("X-Auth-Token", token), Collections.emptyMap(), port);
    }

    public ResponseEntity<JsonNode> updateFloatingIp(String token, String floatingIpId, UpdateFloatingIpRequest request) {
        String uri = "/v2.0/floatingips/" + floatingIpId;
        return openstackAPICallModule.callPutAPI(uri, Collections.singletonMap("X-Auth-Token", token), request, port);
    }

    public ResponseEntity<JsonNode> deleteFloatingIp(String token, String floatingIpId) {
        String uri = "/v2.0/floatingips/" + floatingIpId;
        return openstackAPICallModule.callDeleteAPI(uri, Collections.singletonMap("X-Auth-Token", token), port);
    }

    // Port Forwardings
    public ResponseEntity<JsonNode> listPortForwardings(String token, String floatingIpId, Map<String, String> queryParams) {
        String uri = "/v2.0/floatingips/" + floatingIpId + "/port_forwardings";
        return openstackAPICallModule.callGetAPI(uri, Collections.singletonMap("X-Auth-Token", token), queryParams, port);
    }

    public ResponseEntity<JsonNode> createPortForwarding(String token, String floatingIpId, CreatePortForwardingRequest request) {
        String uri = "/v2.0/floatingips/" + floatingIpId + "/port_forwardings";
        return openstackAPICallModule.callPostAPI(uri, Collections.singletonMap("X-Auth-Token", token), request, port);
    }

    public ResponseEntity<JsonNode> showPortForwarding(String token, String floatingIpId, String portForwardingId) {
        String uri = "/v2.0/floatingips/" + floatingIpId + "/port_forwardings/" + portForwardingId;
        return openstackAPICallModule.callGetAPI(uri, Collections.singletonMap("X-Auth-Token", token), Collections.emptyMap(), port);
    }

    public ResponseEntity<JsonNode> updatePortForwarding(String token, String floatingIpId, String portForwardingId, UpdatePortForwardingRequest request) {
        String uri = "/v2.0/floatingips/" + floatingIpId + "/port_forwardings/" + portForwardingId;
        return openstackAPICallModule.callPutAPI(uri, Collections.singletonMap("X-Auth-Token", token), request, port);
    }

    public ResponseEntity<JsonNode> deletePortForwarding(String token, String floatingIpId, String portForwardingId) {
        String uri = "/v2.0/floatingips/" + floatingIpId + "/port_forwardings/" + portForwardingId;
        return openstackAPICallModule.callDeleteAPI(uri, Collections.singletonMap("X-Auth-Token", token), port);
    }
}