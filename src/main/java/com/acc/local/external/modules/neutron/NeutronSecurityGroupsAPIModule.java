package com.acc.local.external.modules.neutron;

import com.acc.local.external.dto.neutron.securitygroups.CreateSecurityGroupRequest;
import com.acc.local.external.dto.neutron.securitygroups.UpdateSecurityGroupRequest;
import com.acc.local.external.modules.OpenstackAPICallModule;
import com.acc.local.external.dto.neutron.response.NeutronSecurityGroupResponse;
import com.acc.local.external.dto.neutron.response.NeutronSecurityGroupsResponse;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class NeutronSecurityGroupsAPIModule extends NeutronAPIUtil {

    private final OpenstackAPICallModule openstackAPICallModule;

    public ResponseEntity<NeutronSecurityGroupsResponse> listSecurityGroups(String token, Map<String, String> queryParams) {
        String uri = "/v2.0/security-groups";
        return openstackAPICallModule.callGetAPI(uri, Collections.singletonMap("X-Auth-Token", token), queryParams, port, NeutronSecurityGroupsResponse.class);
    }

    public ResponseEntity<NeutronSecurityGroupResponse> createSecurityGroup(String token, CreateSecurityGroupRequest request) {
        String uri = "/v2.0/security-groups";
        return openstackAPICallModule.callPostAPI(uri, Collections.singletonMap("X-Auth-Token", token), request, port, NeutronSecurityGroupResponse.class);
    }

    public ResponseEntity<NeutronSecurityGroupResponse> showSecurityGroup(String token, String securityGroupId) {
        String uri = "/v2.0/security-groups/" + securityGroupId;
        return openstackAPICallModule.callGetAPI(uri, Collections.singletonMap("X-Auth-Token", token), Collections.emptyMap(), port, NeutronSecurityGroupResponse.class);
    }

    public ResponseEntity<JsonNode> updateSecurityGroup(String token, String securityGroupId, UpdateSecurityGroupRequest request) {
        String uri = "/v2.0/security-groups/" + securityGroupId;
        return openstackAPICallModule.callPutAPI(uri, Collections.singletonMap("X-Auth-Token", token), request, port);
    }

    public ResponseEntity<JsonNode> deleteSecurityGroup(String token, String securityGroupId) {
        String uri = "/v2.0/security-groups/" + securityGroupId;
        return openstackAPICallModule.callDeleteAPI(uri, Collections.singletonMap("X-Auth-Token", token), port);
    }
}
