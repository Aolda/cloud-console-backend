package com.acc.local.domain.model.session;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class SessionData {
    private KeycloakTokens keycloakTokens;
    private KeystoneTokens keystoneTokens;
    private String keycloakUserId;
    private String keystoneUserId;
    private UserInfo userInfo;
}
