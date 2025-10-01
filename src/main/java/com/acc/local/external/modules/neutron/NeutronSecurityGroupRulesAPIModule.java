package com.acc.local.external.modules.neutron;

import com.acc.local.external.dto.neutron.securitygroups.BulkCreateSecurityGroupRuleRequest;
import com.acc.local.external.dto.neutron.securitygroups.CreateSecurityGroupRuleRequest;
import com.acc.local.external.modules.OpenstackAPICallModule;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class NeutronSecurityGroupRulesAPIModule extends NeutronAPIUtil {

    private final OpenstackAPICallModule openstackAPICallModule;

    public ResponseEntity<JsonNode> listSecurityGroupRules(String token, Map<String, String> queryParams) {
        String uri = "/v2.0/security-group-rules";
        return openstackAPICallModule.callGetAPI(uri, Collections.singletonMap("X-Auth-Token", token), queryParams, port);
    }

    public ResponseEntity<JsonNode> createSecurityGroupRule(String token, CreateSecurityGroupRuleRequest request) {
        String uri = "/v2.0/security-group-rules";
        return openstackAPICallModule.callPostAPI(uri, Collections.singletonMap("X-Auth-Token", token), request, port);
    }

    public ResponseEntity<JsonNode> bulkCreateSecurityGroupRules(String token, BulkCreateSecurityGroupRuleRequest request) {
        String uri = "/v2.0/security-group-rules";
        return openstackAPICallModule.callPostAPI(uri, Collections.singletonMap("X-Auth-Token", token), request, port);
    }

    public ResponseEntity<JsonNode> showSecurityGroupRule(String token, String securityGroupRuleId) {
        String uri = "/v2.0/security-group-rules/" + securityGroupRuleId;
        return openstackAPICallModule.callGetAPI(uri, Collections.singletonMap("X-Auth-Token", token), Collections.emptyMap(), port);
    }

    public ResponseEntity<JsonNode> deleteSecurityGroupRule(String token, String securityGroupRuleId) {
        String uri = "/v2.0/security-group-rules/" + securityGroupRuleId;
        return openstackAPICallModule.callDeleteAPI(uri, Collections.singletonMap("X-Auth-Token", token), port);
    }
}