package com.acc.local.service.ports;

public interface AuthPort {
    String issueKeystoneToken();
    String authenticateAndGenerateJwt(String userId, String keycloakToken);
    boolean validateJwt(String jwtToken);
    void invalidateUserTokens(String userId);
}
