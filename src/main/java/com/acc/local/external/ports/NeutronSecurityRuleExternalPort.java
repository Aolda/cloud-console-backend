package com.acc.local.external.ports;

public interface NeutronSecurityRuleExternalPort {
    void callCreateSecurityRule(String keystoneToken, String sgId, String direction,
                                String protocol, Integer port,
                                String remoteGroupId, String remoteIpPrefix);

    void callDeleteSecurityRule(String keystoneToken, String srId);
}
