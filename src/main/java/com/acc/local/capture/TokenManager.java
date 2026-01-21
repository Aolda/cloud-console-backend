package com.acc.local.capture;

import com.acc.local.external.adapters.keystone.KeystoneAPIExternalAdapter;
import com.acc.local.dto.auth.KeystonePasswordLoginRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class TokenManager {

    private final KeystoneAPIExternalAdapter keystoneExternalAdapter;

    @Value("${app.capture.keystone.username:}")
    private String username;
    @Value("${app.capture.keystone.password:}")
    private String password;
    @Value("${app.capture.keystone.domain:Default}")
    private String domain;

    private final Map<ScopeType, String> cache = new EnumMap<>(ScopeType.class);

    public String getToken(ScopeType scope, String projectId) {
        try {
            if (cache.containsKey(scope)) return cache.get(scope);
            if (username == null || username.isBlank() || password == null || password.isBlank()) return null;
            KeystonePasswordLoginRequest req = new KeystonePasswordLoginRequest(username, password, domain);
            String token;
            switch (scope) {
                case SYSTEM -> token = keystoneExternalAdapter.getAdminToken(req).token();
                case ADMIN_PROJECT -> token = keystoneExternalAdapter.getAdminTokenWithAdminProjectScope(req).token();
                case PROJECT -> {
                    if (projectId == null || projectId.isBlank()) return null;
                    var unscoped = keystoneExternalAdapter.getUnscopedToken(req);
                    token = keystoneExternalAdapter.getScopedToken(projectId, unscoped.token()).token();
                }
                default -> token = null;
            }
            if (token != null) cache.put(scope, token);
            return token;
        } catch (Exception e) {
            log.warn("[Capture] Failed to obtain {} token: {}", scope, e.toString());
            return null;
        }
    }
}

