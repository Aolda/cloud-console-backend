package com.acc.local.external.modules.keycloak;

import com.acc.local.external.dto.keycloak.KeycloakIntrospectResponse;
import com.acc.local.external.dto.keycloak.KeycloakTokenResponse;
import com.acc.local.external.modules.keycloak.constant.KeycloakRoutes;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

@Component
@RequiredArgsConstructor
public class KeycloakOidcAPIModule {

    private final WebClient keycloakWebClient;

    public KeycloakTokenResponse exchangeCodeForTokens(MultiValueMap<String, String> formData) {
        return keycloakWebClient.post()
                .uri(KeycloakRoutes.TOKEN)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(BodyInserters.fromFormData(formData))
                .retrieve()
                .bodyToMono(KeycloakTokenResponse.class)
                .block();
    }

    public KeycloakTokenResponse refreshTokens(MultiValueMap<String, String> formData) {
        return keycloakWebClient.post()
                .uri(KeycloakRoutes.TOKEN)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(BodyInserters.fromFormData(formData))
                .retrieve()
                .bodyToMono(KeycloakTokenResponse.class)
                .block();
    }

    public KeycloakIntrospectResponse introspectToken(MultiValueMap<String, String> formData) {
        return keycloakWebClient.post()
                .uri(KeycloakRoutes.INTROSPECT)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(BodyInserters.fromFormData(formData))
                .retrieve()
                .bodyToMono(KeycloakIntrospectResponse.class)
                .block();
    }

    public void revokeToken(MultiValueMap<String, String> formData) {
        keycloakWebClient.post()
                .uri(KeycloakRoutes.REVOKE)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(BodyInserters.fromFormData(formData))
                .retrieve()
                .toBodilessEntity()
                .block();
    }
}
