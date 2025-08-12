package com.acc.local.service.ports;

import reactor.core.publisher.Mono;

public interface AuthPort {
    String issueKeystoneToken();
    Mono<String> authenticateAndGenerateJwt(String userId, String keycloakToken);
    boolean validateJwt(String jwtToken);
    void invalidateUserTokens(String userId);
}
