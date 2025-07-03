package com.acc.server.local.service.modules.keystoneToken;

import com.acc.server.local.entity.KeystoneToken;
import com.acc.server.local.model.enums.ScopeType;
import com.acc.server.local.service.modules.keystoneToken.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
@RequiredArgsConstructor
public class KeystoneTokenFetcher {

    private final WebClient keystoneWebClient;

    private static final String TOKEN_URL = "/v3/auth/tokens";

    public KeystoneTokenRequest buildRequestFromScope(
            String username,
            String password,
            String userDomain,
            ScopeType scopeType,
            String projectName,
            String projectDomain,
            String domainName
    ) {
        return switch (scopeType) {
            case PROJECT -> buildProjectScopeRequest(username, password, userDomain, projectName, projectDomain);
            case DOMAIN -> buildDomainScopeRequest(username, password, userDomain, domainName);
            case SYSTEM -> buildSystemScopeRequest(username, password, userDomain);
            case UNSCOPED -> buildUnscopedRequest(username, password, userDomain);
        };
    }

    public KeystoneToken toEntityFromResponse(KeystoneTokenResponse response) {
        // 엔티티 변환 처리 (간단히 예시로 작성)
        return KeystoneToken.builder()
                .id(response.tokenId())
                .expiresAt(response.body().getToken().getExpiresAt())
                .issuedAt(response.body().getToken().getIssuedAt())
                .userName(response.body().getToken().getUser().getName())
                .build();
    }

    private KeystoneTokenRequest buildProjectScopeRequest(
            String username, String password,
            String userDomain, String projectName, String projectDomain
    ) {
        return new KeystoneTokenRequest(
                new KeystoneTokenRequest.Auth(
                        buildIdentity(username, password, userDomain),
                        new KeystoneTokenRequest.Scope(
                                new KeystoneTokenRequest.Project(
                                        projectName,
                                        new KeystoneTokenRequest.Domain(projectDomain)
                                )
                        )
                )
        );
    }

    private KeystoneTokenRequest buildDomainScopeRequest(
            String username, String password,
            String userDomain, String domainName
    ) {
        return new KeystoneTokenRequest(
                new KeystoneTokenRequest.Auth(
                        buildIdentity(username, password, userDomain),
                        new KeystoneTokenRequest.Scope(
                                null, // project
                                new KeystoneTokenRequest.Domain(domainName),
                                null // system
                        )
                )
        );
    }

    private KeystoneTokenRequest buildSystemScopeRequest(String username, String password, String userDomain) {
        return new KeystoneTokenRequest(
                new KeystoneTokenRequest.Auth(
                        buildIdentity(username, password, userDomain),
                        new KeystoneTokenRequest.Scope(
                                null, null,
                                new KeystoneTokenRequest.SystemScope(true)
                        )
                )
        );
    }

    private KeystoneTokenRequest buildUnscopedRequest(String username, String password, String userDomain) {
        return new KeystoneTokenRequest(
                new KeystoneTokenRequest.Auth(
                        buildIdentity(username, password, userDomain),
                        null
                )
        );
    }

    private KeystoneTokenRequest.Identity buildIdentity(String username, String password, String userDomain) {
        return new KeystoneTokenRequest.Identity(
                new String[]{"password"},
                new KeystoneTokenRequest.Password(
                        new KeystoneTokenRequest.User(
                                username,
                                new KeystoneTokenRequest.Domain(userDomain),
                                password
                        )
                )
        );
    }

    public KeystoneTokenResponse sendTokenRequest(KeystoneTokenRequest request) {
        return keystoneWebClient.post()
                .uri(TOKEN_URL)
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
    }
}
