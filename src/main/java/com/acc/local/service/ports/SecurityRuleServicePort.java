package com.acc.local.service.ports;

import com.acc.local.dto.network.CreateSecurityRuleRequest;

public interface SecurityRuleServicePort {
    void createSecurityRule(String sgId, String projectId, String userId, CreateSecurityRuleRequest request);

    void deleteSecurityRule(String ruleId, String projectId, String userId);
}
