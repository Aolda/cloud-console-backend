package com.acc.local.external.modules.neutron;

import com.acc.local.external.dto.neutron.subnets.BulkCreateSubnetRequest;
import com.acc.local.external.dto.neutron.subnets.CreateSubnetRequest;
import com.acc.local.external.dto.neutron.subnets.UpdateSubnetRequest;
import com.acc.local.external.modules.OpenstackAPICallModule;
import com.acc.local.external.dto.neutron.response.NeutronSubnetsResponse;
import com.acc.local.external.dto.neutron.response.NeutronSubnetResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class NeutronSubnetsAPIModule extends NeutronAPIUtil {

    private final OpenstackAPICallModule openstackAPICallModule;

    public ResponseEntity<NeutronSubnetsResponse> listSubnets(String token, Map<String, String> queryParams) {
        String uri = "/v2.0/subnets";
        return openstackAPICallModule.callGetAPI(uri, Collections.singletonMap("X-Auth-Token", token), queryParams, port, NeutronSubnetsResponse.class);
    }

    public ResponseEntity<NeutronSubnetResponse> createSubnet(String token, CreateSubnetRequest request) {
        String uri = "/v2.0/subnets";
        return openstackAPICallModule.callPostAPI(uri, Collections.singletonMap("X-Auth-Token", token), request, port, NeutronSubnetResponse.class);
    }

    public ResponseEntity<NeutronSubnetsResponse> bulkCreateSubnets(String token, BulkCreateSubnetRequest request) {
        String uri = "/v2.0/subnets";
        return openstackAPICallModule.callPostAPI(uri, Collections.singletonMap("X-Auth-Token", token), request, port, NeutronSubnetsResponse.class);
    }

    public ResponseEntity<NeutronSubnetResponse> showSubnet(String token, String subnetId) {
        String uri = "/v2.0/subnets/" + subnetId;
        return openstackAPICallModule.callGetAPI(uri, Collections.singletonMap("X-Auth-Token", token), Collections.emptyMap(), port, NeutronSubnetResponse.class);
    }

    public ResponseEntity<NeutronSubnetResponse> updateSubnet(String token, String subnetId, UpdateSubnetRequest request) {
        String uri = "/v2.0/subnets/" + subnetId;
        return openstackAPICallModule.callPutAPI(uri, Collections.singletonMap("X-Auth-Token", token), request, port, NeutronSubnetResponse.class);
    }

    public ResponseEntity<Void> deleteSubnet(String token, String subnetId) {
        String uri = "/v2.0/subnets/" + subnetId;
        return openstackAPICallModule.callDeleteAPINoBody(uri, Collections.singletonMap("X-Auth-Token", token), port);
    }
}
