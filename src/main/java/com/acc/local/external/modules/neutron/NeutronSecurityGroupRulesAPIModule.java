package com.acc.local.external.modules.neutron;

import com.acc.local.external.dto.neutron.securitygroups.BulkCreateSecurityGroupRuleRequest;
import com.acc.local.external.dto.neutron.securitygroups.CreateSecurityGroupRuleRequest;
import com.acc.local.external.modules.OpenstackAPICallModule;
import com.acc.local.external.dto.neutron.response.NeutronSecurityGroupRulesResponse;
import com.acc.local.external.dto.neutron.response.NeutronSecurityGroupsResponse;
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

    public ResponseEntity<NeutronSecurityGroupRulesResponse> listSecurityGroupRules(String token, Map<String, String> queryParams) {
        String uri = "/v2.0/security-group-rules";
        return openstackAPICallModule.callGetAPI(uri, Collections.singletonMap("X-Auth-Token", token), queryParams, port, NeutronSecurityGroupRulesResponse.class);
    }

    public ResponseEntity<JsonNode> createSecurityGroupRule(String token, CreateSecurityGroupRuleRequest request) {
        String uri = "/v2.0/security-group-rules";
        return openstackAPICallModule.callPostAPI(uri, Collections.singletonMap("X-Auth-Token", token), request, port);
    }

    public ResponseEntity<JsonNode> bulkCreateSecurityGroupRules(String token, BulkCreateSecurityGroupRuleRequest request) {
        String uri = "/v2.0/security-group-rules";
        return openstackAPICallModule.callPostAPI(uri, Collections.singletonMap("X-Auth-Token", token), request, port);
    }

    public ResponseEntity<NeutronSecurityGroupsResponse.SecurityGroupRule> showSecurityGroupRule(String token, String securityGroupRuleId) {
        String uri = "/v2.0/security-group-rules/" + securityGroupRuleId;
        // Neutron show response wraps under {"security_group_rule": {...}} in some deployments.
        // If there is a dedicated wrapper DTO later, we can switch. For now map directly to rule when API returns raw object.
        return openstackAPICallModule.callGetAPI(uri, Collections.singletonMap("X-Auth-Token", token), Collections.emptyMap(), port, NeutronSecurityGroupsResponse.SecurityGroupRule.class);
    }

    public ResponseEntity<JsonNode> deleteSecurityGroupRule(String token, String securityGroupRuleId) {
        String uri = "/v2.0/security-group-rules/" + securityGroupRuleId;
        return openstackAPICallModule.callDeleteAPI(uri, Collections.singletonMap("X-Auth-Token", token), port);
    }
}
