package com.acc.server.local.service.modules.keystoneToken;

import com.acc.server.local.entity.KeystoneToken;
import com.acc.server.local.model.enums.ScopeType;
import com.acc.server.local.service.modules.keystoneToken.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@Component
@RequiredArgsConstructor
public class KeystoneTokenFetcher {

    private final WebClient keystoneWebClient;

    private static final String AUTH_URL = "identity/v3/auth/tokens";

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
            String userId, String password,
            String domainName, Long domainId
    ) {
        return new KeystoneTokenRequest(
                new KeystoneTokenRequest.Auth(
                        buildIdentity(userId, password),
                        new KeystoneTokenRequest.Scope(
                                null,
                                new KeystoneTokenRequest.Domain(domainName, domainId),
                                null
                        )
                )
        );
    }

    public KeystoneTokenRequest buildSystemScopeRequest(String id, String password) {
        return new KeystoneTokenRequest(
                new KeystoneTokenRequest.Auth(
                       buildIdentity(id, password),
                        new KeystoneTokenRequest.Scope(
                                null, null,
                                new KeystoneTokenRequest.SystemScope(true)
                        )
                )
        );
    }

    public KeystoneTokenRequest buildUnscopedRequest(String userId, String password) {
        return new KeystoneTokenRequest(
                new KeystoneTokenRequest.Auth(
                        buildIdentity(userId, password),
                        null
                )
        );
    }

    private KeystoneTokenRequest.Identity buildIdentity(String userId, String password) {
        return new KeystoneTokenRequest.Identity(
                new String[]{"password"},
                new KeystoneTokenRequest.Password(
                        new KeystoneTokenRequest.User(
                                userId,
                                password
                        )
                )
        );
    }

    public KeystoneTokenResponse sendTokenRequest(KeystoneTokenRequest request) {
        try {
            return keystoneWebClient.post()
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
