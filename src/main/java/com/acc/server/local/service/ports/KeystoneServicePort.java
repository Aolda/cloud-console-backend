package com.acc.server.local.service.ports;

import com.acc.server.local.entity.KeystoneToken;
import com.acc.server.local.model.enums.ScopeType;

public interface KeystoneServicePort {

    KeystoneToken fetchToken(String username, String password, String userDomain, ScopeType scopeType, String projectName, String projectDomain, String domainName);
}
