package com.acc.local.service.ports;

import com.acc.local.domain.enums.ProjectPermission;
import reactor.core.publisher.Mono;

public interface AuthPort {
    String issueKeystoneToken();
    String authenticateAndGenerateJwt(String keycloakToken);
    boolean validateJwt(String jwtToken);
    void invalidateUserTokens(String userId);
    ProjectPermission getProjectPermission(String ProjectId , String userId);
}
