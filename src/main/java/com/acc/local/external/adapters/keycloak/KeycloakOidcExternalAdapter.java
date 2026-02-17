package com.acc.local.external.adapters.keycloak;

import com.acc.global.exception.auth.KeycloakErrorCode;
import com.acc.global.exception.auth.KeycloakException;
import com.acc.global.properties.KeycloakProperties;
import com.acc.local.external.dto.keycloak.*;
import com.acc.local.external.modules.keycloak.KeycloakOidcAPIModule;
import com.acc.local.external.modules.keycloak.constant.KeycloakRoutes;
import com.acc.local.external.ports.KeycloakOidcExternalPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.util.UriComponentsBuilder;

@Component
@RequiredArgsConstructor
@Slf4j
public class KeycloakOidcExternalAdapter implements KeycloakOidcExternalPort {

    private final KeycloakOidcAPIModule keycloakOidcAPIModule;
    private final KeycloakProperties keycloakProperties;

    @Override
    public String getAuthorizationUrl(String state) {
        return UriComponentsBuilder.fromHttpUrl(keycloakProperties.getIssuerUri() + KeycloakRoutes.AUTHORIZATION)
                .queryParam("client_id", keycloakProperties.getClientId())
                .queryParam("response_type", "code")
                .queryParam("scope", "openid")
                .queryParam("state", state)
                .toUriString();
    }

    @Override
    public KeycloakTokenResponse exchangeCodeForTokens(String code, String redirectUri) {
        try {
            KeycloakTokenExchangeRequest request = KeycloakTokenExchangeRequest.builder()
                    .code(code)
                    .redirectUri(redirectUri)
                    .clientId(keycloakProperties.getClientId())
                    .clientSecret(keycloakProperties.getClientSecret())
                    .build();
            return keycloakOidcAPIModule.exchangeCodeForTokens(request.toFormData());
        } catch (WebClientResponseException e) {
            log.error("Keycloak 토큰 교환 실패: {}", e.getResponseBodyAsString(), e);
            throw new KeycloakException(KeycloakErrorCode.KEYCLOAK_TOKEN_EXCHANGE_FAILED, e);
        }
    }

    @Override
    public KeycloakTokenResponse refreshTokens(String refreshToken) {
        try {
            KeycloakTokenRefreshRequest request = KeycloakTokenRefreshRequest.builder()
                    .refreshToken(refreshToken)
                    .clientId(keycloakProperties.getClientId())
                    .clientSecret(keycloakProperties.getClientSecret())
                    .build();
            return keycloakOidcAPIModule.refreshTokens(request.toFormData());
        } catch (WebClientResponseException e) {
            log.error("Keycloak 토큰 갱신 실패: {}", e.getResponseBodyAsString(), e);
            throw new KeycloakException(KeycloakErrorCode.KEYCLOAK_TOKEN_REFRESH_FAILED, e);
        }
    }

    @Override
    public KeycloakIntrospectResponse introspectToken(String token) {
        try {
            KeycloakIntrospectRequest request = KeycloakIntrospectRequest.builder()
                    .token(token)
                    .clientId(keycloakProperties.getClientId())
                    .clientSecret(keycloakProperties.getClientSecret())
                    .build();
            return keycloakOidcAPIModule.introspectToken(request.toFormData());
        } catch (WebClientResponseException e) {
            log.error("Keycloak 토큰 검증 실패: {}", e.getResponseBodyAsString(), e);
            throw new KeycloakException(KeycloakErrorCode.KEYCLOAK_TOKEN_INTROSPECT_FAILED, e);
        }
    }
}
