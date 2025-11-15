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
import org.springframework.web.reactive.function.client.WebClientException;

@Component
@RequiredArgsConstructor
@Slf4j
public class NeutronSecurityRuleExternalAdapter implements NeutronSecurityRuleExternalPort {

    private final NeutronSecurityGroupRulesAPIModule securityGroupRulesAPIModule;

    @Override
    public void callCreateSecurityRule(String keystoneToken, String sgId, String direction,
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

        } catch (WebClientException e) {
            throw new NeutronException(NeutronErrorCode.NEUTRON_SECURITY_RULE_CREATION_FAILED);
        }

    }

    @Override
    public void callDeleteSecurityRule(String keystoneToken, String srId) {
        try {
            ResponseEntity<JsonNode> response = securityGroupRulesAPIModule.deleteSecurityGroupRule(keystoneToken, srId);

            if (!response.getStatusCode().is2xxSuccessful()) {
                throw new NeutronException(NeutronErrorCode.NEUTRON_SECURITY_RULE_DELETION_FAILED);
            }

        } catch (WebClientException e) {
            throw new NeutronException(NeutronErrorCode.NEUTRON_SECURITY_RULE_DELETION_FAILED);
        }
    }
}
