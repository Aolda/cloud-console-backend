package com.acc.server.local.service.modules.keystoneToken;

import com.acc.server.local.entity.KeystoneToken;
import com.acc.server.local.service.modules.keystoneToken.dto.*;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@Component
public class KeystoneTokenFetcher {

    private final WebClient webClient;
    private final KeystoneApiProperties properties;

    public KeystoneTokenFetcher(KeystoneApiProperties properties) {
        this.properties = properties;
        this.webClient = WebClient.builder()
                .baseUrl(properties.getBaseUrl())
                .build();
    }

    public KeystoneToken toEntityFromResponse(KeystoneTokenResponse response) {
        return KeystoneToken.builder()
                // 응답 dto -> 엔티티 매핑 로직 필요
                .build();
    }

    public KeystoneTokenRequest buildProjectScopeRequest(
            String username, String password, String projectName, Long projectId, String domainName, Long domainId
    ) {
        return new KeystoneTokenRequest(
                new KeystoneTokenRequest.Auth(
                        buildIdentity(username, password),
                        new KeystoneTokenRequest.Scope(
                                new KeystoneTokenRequest.Project(
                                        new KeystoneTokenRequest.Domain(domainName, domainId),
                                        projectName,
                                        projectId
                                ), null, null
                        )
                )
        );
    }

    public KeystoneTokenRequest buildDomainScopeRequest(
            String username, String password,
            String domainName, Long domainId
    ) {
        return new KeystoneTokenRequest(
                new KeystoneTokenRequest.Auth(
                        buildIdentity(username, password),
                        new KeystoneTokenRequest.Scope(
                                null,
                                new KeystoneTokenRequest.Domain(domainName, domainId),
                                null
                        )
                )
        );
    }

    public KeystoneTokenRequest buildSystemScopeRequest(String name, String password) {
        return new KeystoneTokenRequest(
                new KeystoneTokenRequest.Auth(
                       buildIdentity(name, password),
                        new KeystoneTokenRequest.Scope(
                                null, null,
                                new KeystoneTokenRequest.SystemScope(true)
                        )
                )
        );
    }

    public KeystoneTokenRequest buildUnscopedRequest(String username, String password) {
        return new KeystoneTokenRequest(
                new KeystoneTokenRequest.Auth(
                        buildIdentity(username, password),
                        null
                )
        );
    }

    private KeystoneTokenRequest.Identity buildIdentity(String username, String password) {
        return new KeystoneTokenRequest.Identity(
                new String[]{"password"},
                new KeystoneTokenRequest.Password(
                        new KeystoneTokenRequest.User(
                                username,
                                password,
                                new KeystoneTokenRequest.Domain(
                                        "default",
                                        null
                                )
                        )
                )
        );
    }

    public KeystoneTokenResponse sendTokenRequest(KeystoneTokenRequest request) {
        String AUTH_URL = properties.getTokenUrl();
        try {
            return webClient.post()
                    .uri(AUTH_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .bodyValue(request)
                    .retrieve()
                    .toEntity(KeystoneTokenResponseBody.class)
                    .map(responseEntity -> new KeystoneTokenResponse(
                            responseEntity.getHeaders().getFirst("X-Subject-Token"),
                            responseEntity.getBody()
                    ))
                    .block();
        } catch (WebClientResponseException e) {
            //커스텀 에러 처리 들어갈 부분
            throw new RuntimeException("Keystone API 응답 에러: " + e.getStatusCode() + " - " + e.getResponseBodyAsString(), e);
        } catch (Exception e) {
            throw new RuntimeException("Keystone API 요청 중 예외 발생", e);
        }
    }

}
