package com.acc.server.local.service.adapters;

import com.acc.server.local.entity.KeystoneToken;
import com.acc.server.local.service.modules.keystoneToken.KeystoneTokenFetcher;
import com.acc.server.local.service.modules.keystoneToken.dto.KeystoneTokenRequest;
import com.acc.server.local.service.modules.keystoneToken.dto.KeystoneTokenResponse;
import com.acc.server.local.service.ports.KeystoneServicePort;

public class KeystoneServiceAdapter implements KeystoneServicePort {

    private KeystoneTokenFetcher tokenFetcher;

    @Override
    public KeystoneToken fetchSystemScopeToken(String username, String password){
        KeystoneTokenRequest req = tokenFetcher.buildSystemScopeRequest(username, password);
        KeystoneTokenResponse res = tokenFetcher.sendTokenRequest(req);
        return tokenFetcher.toEntityFromResponse(res);
    }

    @Override
    public KeystoneToken fetchUnscopedToken(String username, String password){
        KeystoneTokenRequest req = tokenFetcher.buildUnscopedRequest(username, password);
        KeystoneTokenResponse res = tokenFetcher.sendTokenRequest(req);
        return tokenFetcher.toEntityFromResponse(res);
    }

    @Override
    public KeystoneToken fetchProjectScopeToken(String username, String password, String projectName, Long projectId, String domainName, Long domainId){
        KeystoneTokenRequest req = tokenFetcher.buildProjectScopeRequest(username, password, projectName, projectId, domainName, domainId);
        KeystoneTokenResponse res = tokenFetcher.sendTokenRequest(req);
        return tokenFetcher.toEntityFromResponse(res);
    }

    @Override
    public KeystoneToken fetchDomainScopeToken(String username, String password, String domainName, Long domainId){
        KeystoneTokenRequest req = tokenFetcher.buildDomainScopeRequest(username, password, domainName, domainId);
        KeystoneTokenResponse res = tokenFetcher.sendTokenRequest(req);
        return tokenFetcher.toEntityFromResponse(res);
    }

}
