package com.acc.local.service.adapters.auth;

import com.acc.local.service.modules.keycloak.KeycloakAuthModule;
import com.acc.local.service.ports.KeycloakAuthServicePort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class KeycloakAuthServiceAdapter implements KeycloakAuthServicePort {

    private final KeycloakAuthModule keycloakAuthModule;

    @Override
    public String buildAuthorizationUrl(String state) {
        return keycloakAuthModule.buildAuthorizationUrl(state);
    }

    @Override
    public String processCallback(String code) {
        return keycloakAuthModule.processCallback(code);
    }

    @Override
    public void logout(String sessionId) {
        keycloakAuthModule.logout(sessionId);
    }
}
