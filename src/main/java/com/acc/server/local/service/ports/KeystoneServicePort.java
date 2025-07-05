package com.acc.server.local.service.ports;

import com.acc.server.local.entity.KeystoneToken;
import com.acc.server.local.model.enums.ScopeType;

public interface KeystoneServicePort {

    KeystoneToken fetchSystemScopeToken(String username, String password);
    KeystoneToken fetchUnscopedToken(String username, String password);
    KeystoneToken fetchProjectScopeToken(String username, String password, String projectName, Long projectId, String domainName, Long domainId);
    KeystoneToken fetchDomainScopeToken(String username, String password, String domainName, Long domainId);
}

