package com.acc.local.external.ports;

import com.acc.local.external.dto.keycloak.KeycloakIntrospectResponse;
import com.acc.local.external.dto.keycloak.KeycloakTokenResponse;

public interface KeycloakOidcExternalPort {

    String getAuthorizationUrl(String state);

    KeycloakTokenResponse exchangeCodeForTokens(String code, String redirectUri);

    KeycloakTokenResponse refreshTokens(String refreshToken);

    KeycloakIntrospectResponse introspectToken(String token);
}
