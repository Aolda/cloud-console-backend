package com.acc.local.capture;

import com.acc.local.external.modules.OpenstackAPICallModule;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClientRequestException;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class ExtraGetCaptureService {

    private final OpenstackAPICallModule openstackAPICallModule;
    private final CaptureStorage captureStorage;
    private final CaptureRateLimiter rateLimiter;

    public void capture(String component, int port, String token, List<String> uris) {
        if (uris == null || uris.isEmpty()) return;
        for (String uri : uris) {
            rateLimiter.pause();
            try {
                ResponseEntity<JsonNode> resp = null;
                int attempts = Math.max(0, rateLimiter.getRetries()) + 1;
                for (int i = 0; i < attempts; i++) {
                    try {
                        resp = openstackAPICallModule.callGetAPI(uri,
                                Collections.singletonMap("X-Auth-Token", token), Collections.emptyMap(), port);
                        break;
                    } catch (WebClientRequestException cre) {
                        if (i < attempts - 1) rateLimiter.backoffSleep(i + 1);
                        else throw cre;
                    }
                }
                JsonNode body = resp != null ? resp.getBody() : null;
                int status = resp != null && resp.getStatusCode() != null ? resp.getStatusCode().value() : -1;
                captureStorage.save(component, sanitizeName(uri), "GET", uri, port,
                        Map.of("X-Auth-Token", "__MASKED__"), Collections.emptyMap(), status, body);
                rateLimiter.maybeCooldown();
            } catch (Exception e) {
                log.warn("[Capture] Extra GET failed for {} {}:{}", component, uri, port, e);
            }
        }
    }

    public void captureWithScope(String component, int port, List<String> uris, TokenManager tokenManager,
                                 String projectId, ScopeType[] order) {
        if (uris == null || uris.isEmpty()) return;
        for (String uri : uris) {
            boolean done = false;
            for (ScopeType scope : order) {
                rateLimiter.pause();
                String t = tokenManager.getToken(scope, projectId);
                if (t == null || t.isBlank()) continue;
                Map<String, String> headers = Map.of(
                        "X-Auth-Token", t,
                        "X-Auth-Token-Scope", scope == ScopeType.SYSTEM ? "system" : (scope == ScopeType.ADMIN_PROJECT ? "admin-project" : "project")
                );
                try {
                    ResponseEntity<JsonNode> resp = null;
                    int attempts = Math.max(0, rateLimiter.getRetries()) + 1;
                    for (int i = 0; i < attempts; i++) {
                        try {
                            resp = openstackAPICallModule.callGetAPI(uri, headers, Collections.emptyMap(), port);
                            break;
                        } catch (WebClientRequestException cre) {
                            if (i < attempts - 1) rateLimiter.backoffSleep(i + 1);
                            else throw cre;
                        }
                    }
                    JsonNode body = resp != null ? resp.getBody() : null;
                    int status = resp != null && resp.getStatusCode() != null ? resp.getStatusCode().value() : -1;
                    Map<String, String> storedHeaders = Map.of(
                            "X-Auth-Token", "__MASKED__",
                            "X-Auth-Token-Scope", headers.get("X-Auth-Token-Scope")
                    );
                    captureStorage.save(component, sanitizeName(uri), "GET", uri, port, storedHeaders, Collections.emptyMap(), status, body);
                    rateLimiter.maybeCooldown();
                    done = true;
                    break;
                } catch (Exception e) {
                    // try next scope
                }
            }
            if (!done) {
                captureStorage.save(component, sanitizeName(uri), "GET", uri, port,
                        Map.of("X-Auth-Token", "__MASKED__"), Collections.emptyMap(), -1, null);
            }
        }
    }

    private String sanitizeName(String uri) {
        return uri.replaceAll("/", "_").replaceAll("[^a-zA-Z0-9._-]", "_");
    }
}
