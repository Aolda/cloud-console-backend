package com.acc.local.service.ports;

import com.acc.local.domain.enums.ProjectPermission;

public interface AuthServicePort {
    String issueKeystoneToken();
    String authenticateAndGenerateJwt(String keycloakToken);
    ProjectPermission getProjectPermission(String ProjectId , String userId);
    boolean validateJwt(String jwtToken);
    void invalidateUserTokens(String userId);
}
