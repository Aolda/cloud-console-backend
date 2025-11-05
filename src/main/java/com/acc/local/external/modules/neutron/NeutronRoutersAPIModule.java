package com.acc.local.external.modules.neutron;

import com.acc.local.external.dto.neutron.conntrackhelpers.CreateConntrackHelperRequest;
import com.acc.local.external.dto.neutron.conntrackhelpers.UpdateConntrackHelperRequest;
import com.acc.local.external.dto.neutron.routers.*;
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
public class NeutronRoutersAPIModule extends NeutronAPIUtil {

    private final OpenstackAPICallModule openstackAPICallModule;

    public ResponseEntity<JsonNode> listRouters(String token, Map<String, String> queryParams) {
        String uri = "/v2.0/routers";
        return openstackAPICallModule.callGetAPI(uri, Collections.singletonMap("X-Auth-Token", token), queryParams, port);
    }

    public ResponseEntity<JsonNode> listRouters(String token, MultiValueMap<String, String> queryParams) {
        String uri = "/v2.0/routers";
        return openstackAPICallModule.callGetAPI(uri, Collections.singletonMap("X-Auth-Token", token), queryParams, port);
    }

    public ResponseEntity<JsonNode> createRouter(String token, CreateRouterRequest request) {
        String uri = "/v2.0/routers";
        return openstackAPICallModule.callPostAPI(uri, Collections.singletonMap("X-Auth-Token", token), request, port);
    }

    public ResponseEntity<JsonNode> showRouter(String token, String routerId) {
        String uri = "/v2.0/routers/" + routerId;
        return openstackAPICallModule.callGetAPI(uri, Collections.singletonMap("X-Auth-Token", token), Collections.emptyMap(), port);
    }

    public ResponseEntity<JsonNode> updateRouter(String token, String routerId, UpdateRouterRequest request) {
        String uri = "/v2.0/routers/" + routerId;
        return openstackAPICallModule.callPutAPI(uri, Collections.singletonMap("X-Auth-Token", token), request, port);
    }

    public ResponseEntity<JsonNode> deleteRouter(String token, String routerId) {
        String uri = "/v2.0/routers/" + routerId;
        return openstackAPICallModule.callDeleteAPI(uri, Collections.singletonMap("X-Auth-Token", token), port);
    }

    public ResponseEntity<JsonNode> addRouterInterface(String token, String routerId, AddRouterInterfaceRequest request) {
        String uri = "/v2.0/routers/" + routerId + "/add_router_interface";
        return openstackAPICallModule.callPutAPI(uri, Collections.singletonMap("X-Auth-Token", token), request, port);
    }

    public ResponseEntity<JsonNode> removeRouterInterface(String token, String routerId, RemoveRouterInterfaceRequest request) {
        String uri = "/v2.0/routers/" + routerId + "/remove_router_interface";
        return openstackAPICallModule.callPutAPI(uri, Collections.singletonMap("X-Auth-Token", token), request, port);
    }

    public ResponseEntity<JsonNode> addExtraRoutesToRouter(String token, String routerId, AddExtraRoutesRequest request) {
        String uri = "/v2.0/routers/" + routerId + "/add_extraroutes";
        return openstackAPICallModule.callPutAPI(uri, Collections.singletonMap("X-Auth-Token", token), request, port);
    }

    public ResponseEntity<JsonNode> removeExtraRoutesFromRouter(String token, String routerId, RemoveExtraRoutesRequest request) {
        String uri = "/v2.0/routers/" + routerId + "/remove_extraroutes";
        return openstackAPICallModule.callPutAPI(uri, Collections.singletonMap("X-Auth-Token", token), request, port);
    }

    public ResponseEntity<JsonNode> addExternalGatewaysToRouter(String token, String routerId, AddExternalGatewaysRequest request) {
        String uri = "/v2.0/routers/" + routerId + "/add_external_gateways";
        return openstackAPICallModule.callPutAPI(uri, Collections.singletonMap("X-Auth-Token", token), request, port);
    }

    public ResponseEntity<JsonNode> updateExternalGatewaysOfRouter(String token, String routerId, UpdateExternalGatewaysRequest request) {
        String uri = "/v2.0/routers/" + routerId + "/update_external_gateways";
        return openstackAPICallModule.callPutAPI(uri, Collections.singletonMap("X-Auth-Token", token), request, port);
    }

    public ResponseEntity<JsonNode> removeExternalGatewaysFromRouter(String token, String routerId, RemoveExternalGatewaysRequest request) {
        String uri = "/v2.0/routers/" + routerId + "/remove_external_gateways";
        return openstackAPICallModule.callPutAPI(uri, Collections.singletonMap("X-Auth-Token", token), request, port);
    }

    // Conntrack Helpers
    public ResponseEntity<JsonNode> listConntrackHelpers(String token, String routerId) {
        String uri = "/v2.0/routers/" + routerId + "/conntrack_helpers";
        return openstackAPICallModule.callGetAPI(uri, Collections.singletonMap("X-Auth-Token", token), Collections.emptyMap(), port);
    }

    public ResponseEntity<JsonNode> createConntrackHelper(String token, String routerId, CreateConntrackHelperRequest request) {
        String uri = "/v2.0/routers/" + routerId + "/conntrack_helpers";
        return openstackAPICallModule.callPostAPI(uri, Collections.singletonMap("X-Auth-Token", token), request, port);
    }

    public ResponseEntity<JsonNode> showConntrackHelper(String token, String routerId, String conntrackHelperId) {
        String uri = "/v2.0/routers/" + routerId + "/conntrack_helpers/" + conntrackHelperId;
        return openstackAPICallModule.callGetAPI(uri, Collections.singletonMap("X-Auth-Token", token), Collections.emptyMap(), port);
    }

    public ResponseEntity<JsonNode> updateConntrackHelper(String token, String routerId, String conntrackHelperId, UpdateConntrackHelperRequest request) {
        String uri = "/v2.0/routers/" + routerId + "/conntrack_helpers/" + conntrackHelperId;
        return openstackAPICallModule.callPutAPI(uri, Collections.singletonMap("X-Auth-Token", token), request, port);
    }

    public ResponseEntity<JsonNode> deleteConntrackHelper(String token, String routerId, String conntrackHelperId) {
        String uri = "/v2.0/routers/" + routerId + "/conntrack_helpers/" + conntrackHelperId;
        return openstackAPICallModule.callDeleteAPI(uri, Collections.singletonMap("X-Auth-Token", token), port);
    }
}