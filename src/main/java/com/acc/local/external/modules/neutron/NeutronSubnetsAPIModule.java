package com.acc.local.external.modules.neutron;

import com.acc.local.external.dto.neutron.subnets.BulkCreateSubnetRequest;
import com.acc.local.external.dto.neutron.subnets.CreateSubnetRequest;
import com.acc.local.external.dto.neutron.subnets.UpdateSubnetRequest;
import com.acc.local.external.modules.OpenstackAPICallModule;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class NeutronSubnetsAPIModule extends NeutronAPIUtil {

    private final OpenstackAPICallModule openstackAPICallModule;

    public ResponseEntity<JsonNode> listSubnets(String token, Map<String, String> queryParams) {
        String uri = "/v2.0/subnets";
        return openstackAPICallModule.callGetAPI(uri, Collections.singletonMap("X-Auth-Token", token), queryParams, port);
    }

    public ResponseEntity<JsonNode> createSubnet(String token, CreateSubnetRequest request) {
        String uri = "/v2.0/subnets";
        return openstackAPICallModule.callPostAPI(uri, Collections.singletonMap("X-Auth-Token", token), request, port);
    }

    public ResponseEntity<JsonNode> bulkCreateSubnets(String token, BulkCreateSubnetRequest request) {
        String uri = "/v2.0/subnets";
        return openstackAPICallModule.callPostAPI(uri, Collections.singletonMap("X-Auth-Token", token), request, port);
    }

    public ResponseEntity<JsonNode> showSubnet(String token, String subnetId) {
        String uri = "/v2.0/subnets/" + subnetId;
        return openstackAPICallModule.callGetAPI(uri, Collections.singletonMap("X-Auth-Token", token), Collections.emptyMap(), port);
    }

    public ResponseEntity<JsonNode> updateSubnet(String token, String subnetId, UpdateSubnetRequest request) {
        String uri = "/v2.0/subnets/" + subnetId;
        return openstackAPICallModule.callPutAPI(uri, Collections.singletonMap("X-Auth-Token", token), request, port);
    }

    public ResponseEntity<JsonNode> deleteSubnet(String token, String subnetId) {
        String uri = "/v2.0/subnets/" + subnetId;
        return openstackAPICallModule.callDeleteAPI(uri, Collections.singletonMap("X-Auth-Token", token), port);
    }
}