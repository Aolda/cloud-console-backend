package com.acc.local.external.modules.neutron;

import com.acc.local.external.dto.neutron.subnetpools.*;
import com.acc.local.external.modules.OpenstackAPICallModule;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class NeutronSubnetPoolsAPIModule extends NeutronAPIUtil {

    private final OpenstackAPICallModule openstackAPICallModule;

    public ResponseEntity<JsonNode> listSubnetPools(String token, Map<String, String> queryParams) {
        String uri = "/v2.0/subnetpools";
        return openstackAPICallModule.callGetAPI(uri, Collections.singletonMap("X-Auth-Token", token), queryParams, port);
    }

    public ResponseEntity<JsonNode> createSubnetPool(String token, CreateSubnetPoolRequest request) {
        String uri = "/v2.0/subnetpools";
        return openstackAPICallModule.callPostAPI(uri, Collections.singletonMap("X-Auth-Token", token), request, port);
    }

    public ResponseEntity<JsonNode> showSubnetPool(String token, String subnetPoolId) {
        String uri = "/v2.0/subnetpools/" + subnetPoolId;
        return openstackAPICallModule.callGetAPI(uri, Collections.singletonMap("X-Auth-Token", token), Collections.emptyMap(), port);
    }

    public ResponseEntity<JsonNode> updateSubnetPool(String token, String subnetPoolId, UpdateSubnetPoolRequest request) {
        String uri = "/v2.0/subnetpools/" + subnetPoolId;
        return openstackAPICallModule.callPutAPI(uri, Collections.singletonMap("X-Auth-Token", token), request, port);
    }

    public ResponseEntity<JsonNode> deleteSubnetPool(String token, String subnetPoolId) {
        String uri = "/v2.0/subnetpools/" + subnetPoolId;
        return openstackAPICallModule.callDeleteAPI(uri, Collections.singletonMap("X-Auth-Token", token), port);
    }

    public ResponseEntity<JsonNode> addPrefixesToSubnetPool(String token, String subnetPoolId, AddSubnetPoolPrefixesRequest request) {
        String uri = "/v2.0/subnetpools/" + subnetPoolId + "/add_prefixes";
        return openstackAPICallModule.callPutAPI(uri, Collections.singletonMap("X-Auth-Token", token), request, port);
    }

    public ResponseEntity<JsonNode> removePrefixesFromSubnetPool(String token, String subnetPoolId, RemoveSubnetPoolPrefixesRequest request) {
        String uri = "/v2.0/subnetpools/" + subnetPoolId + "/remove_prefixes";
        return openstackAPICallModule.callPutAPI(uri, Collections.singletonMap("X-Auth-Token", token), request, port);
    }

    public ResponseEntity<JsonNode> onboardNeutronSubnets(String token, String subnetPoolId, OnboardNetworkSubnetsRequest request) {
        String uri = "/v2.0/subnetpools/" + subnetPoolId + "/onboard_network_subnets";
        return openstackAPICallModule.callPutAPI(uri, Collections.singletonMap("X-Auth-Token", token), request, port);
    }
}