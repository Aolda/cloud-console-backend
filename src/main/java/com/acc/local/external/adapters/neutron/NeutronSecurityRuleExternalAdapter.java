package com.acc.local.external.adapters.neutron;

import com.acc.global.exception.network.NeutronErrorCode;
import com.acc.global.exception.network.NeutronException;
import com.acc.local.external.dto.neutron.securitygroups.CreateSecurityGroupRuleRequest;
import com.acc.local.external.modules.neutron.NeutronSecurityGroupRulesAPIModule;
import com.acc.local.external.ports.NeutronSecurityRuleExternalPort;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@Component
@RequiredArgsConstructor
@Slf4j
public class NeutronSecurityRuleExternalAdapter implements NeutronSecurityRuleExternalPort {

    private final NeutronSecurityGroupRulesAPIModule securityGroupRulesAPIModule;

    @Override
    public String callCreateSecurityRule(String keystoneToken, String sgId, String direction,
                                       String protocol, Integer port,
                                       String remoteGroupId, String remoteIpPrefix) {
        try {
            ResponseEntity<JsonNode> response = securityGroupRulesAPIModule.createSecurityGroupRule(keystoneToken,
                    CreateSecurityGroupRuleRequest.builder()
                            .securityGroupRule(
                                    CreateSecurityGroupRuleRequest.SecurityGroupRule.builder()
                                            .securityGroupId(sgId)
                                            .direction(direction)
                                            .protocol(protocol)
                                            .portRangeMin(port)
                                            .portRangeMax(port)
                                            .remoteGroupId(remoteGroupId)
                                            .remoteIpPrefix(remoteIpPrefix)
                                            .build()
                            )
                            .build());

            if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
                throw new NeutronException(NeutronErrorCode.NEUTRON_SECURITY_RULE_CREATION_FAILED);
            }

            JsonNode securityRuleNode = response.getBody().get("security_group_rule");
            return securityRuleNode.get("id").asText();
        } catch (WebClientResponseException e) {
            log.error(e.getMessage(), e.getResponseBodyAsString(), e);
            switch (e.getStatusCode().value()) {
                case 400 -> throw new NeutronException(NeutronErrorCode.NEUTRON_SECURITY_RULE_BAD_REQUEST, e);
                case 403 -> throw new NeutronException(NeutronErrorCode.NEUTRON_SECURITY_RULE_FORBIDDEN, e);
                default -> throw new NeutronException(NeutronErrorCode.NEUTRON_SECURITY_RULE_CREATION_FAILED, e);
            }
        }

    }

    @Override
    public void callDeleteSecurityRule(String keystoneToken, String srId) {
        try {
            ResponseEntity<JsonNode> response = securityGroupRulesAPIModule.deleteSecurityGroupRule(keystoneToken, srId);

            if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
                throw new NeutronException(NeutronErrorCode.NEUTRON_SECURITY_RULE_DELETION_FAILED);
            }
        } catch (WebClientResponseException e) {
            log.error(e.getMessage(), e.getResponseBodyAsString(), e);
            switch (e.getStatusCode().value()) {
                case 400 -> throw new NeutronException(NeutronErrorCode.NEUTRON_SECURITY_RULE_BAD_REQUEST, e);
                case 403 -> throw new NeutronException(NeutronErrorCode.NEUTRON_SECURITY_RULE_FORBIDDEN, e);
                case 404 -> throw new NeutronException(NeutronErrorCode.NEUTRON_SECURITY_RULE_NOT_FOUND, e);
                default -> throw new NeutronException(NeutronErrorCode.NEUTRON_SECURITY_RULE_DELETION_FAILED, e);
            }
        }
    }
}
