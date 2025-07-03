package com.acc.server.local.service.adapters;

import com.acc.server.local.entity.KeystoneToken;
import com.acc.server.local.model.enums.ScopeType;
import com.acc.server.local.service.modules.keystoneToken.KeystoneTokenFetcher;
import com.acc.server.local.service.modules.keystoneToken.dto.KeystoneTokenRequest;
import com.acc.server.local.service.modules.keystoneToken.dto.KeystoneTokenResponse;
import com.acc.server.local.service.ports.KeystoneServicePort;

public class KeystoneServiceAdapter implements KeystoneServicePort {

    private KeystoneTokenFetcher tokenFetcher;

    @Override
    public KeystoneToken fetchToken(String username, String password, String userDomain, ScopeType scopeType, String projectName, String projectDomain, String domainName) {
        KeystoneTokenRequest req = tokenFetcher.buildRequestFromScope(username, password, userDomain, scopeType, projectName, projectDomain, domainName);
        KeystoneTokenResponse res = tokenFetcher.sendTokenRequest(req);
        return tokenFetcher.toEntityFromResponse(res);
    }
}
