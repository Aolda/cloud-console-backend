package com.acc.local.service.ports;

import com.acc.local.dto.network.CreateSecurityRuleRequest;

public interface SecurityRuleServicePort {
    String createSecurityRule(String projectId, String userId, CreateSecurityRuleRequest request);

    void deleteSecurityRule(String ruleId, String projectId, String userId);
}
