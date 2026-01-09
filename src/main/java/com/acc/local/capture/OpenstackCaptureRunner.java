package com.acc.local.capture;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Slf4j
@Component
@Profile("capture")
@RequiredArgsConstructor
public class OpenstackCaptureRunner implements ApplicationRunner {

    private final NeutronCaptureScenarios neutronCaptureScenarios;
    private final NovaCaptureScenarios novaCaptureScenarios;
    private final GlanceCaptureScenarios glanceCaptureScenarios;
    private final CinderCaptureScenarios cinderCaptureScenarios;
    private final CaptureStorage captureStorage;
    private final com.acc.local.external.modules.OpenstackAPICallModule openstackAPICallModule;
    private final TokenManager tokenManager;
    private final com.acc.local.external.adapters.keystone.KeystoneAPIExternalAdapter keystoneExternalAdapter;
    private final NeutronWriteCaptureService neutronWriteCaptureService;
    private final NovaWriteCaptureService novaWriteCaptureService;
    private final ExtraGetCaptureService extraGetCaptureService;
    private final CaptureIndex captureIndex;
    private final AutoFollowUpCaptureService autoFollowUpCaptureService;
    private final GetSweepService getSweepService;
    private final CaptureRateLimiter rateLimiter;

    @Value("${app.capture.enabled:true}")
    private boolean enabled;

    @Value("${app.capture.token:}")
    private String token;
    @Value("${app.capture.keystone.username:}")
    private String username;
    @Value("${app.capture.keystone.password:}")
    private String password;
    @Value("${app.capture.keystone.domain:Default}")
    private String domain;

    @Value("${app.capture.project-id:}")
    private String projectId;

    @Value("${app.capture.prefer-token-scope:project}")
    private String preferTokenScope; // project | system | admin-project

    @Value("${app.capture.neutron.networks:true}")
    private boolean captureNetworks;
    @Value("${app.capture.neutron.ports:true}")
    private boolean capturePorts;
    @Value("${app.capture.neutron.security-groups:true}")
    private boolean captureSecurityGroups;
    @Value("${app.capture.neutron.security-group-rules:true}")
    private boolean captureSecurityGroupRules;
    @Value("${app.capture.neutron.subnets:true}")
    private boolean captureSubnets;
    @Value("${app.capture.neutron.routers:true}")
    private boolean captureRouters;
    @Value("${app.capture.neutron.floatingips:true}")
    private boolean captureFloatingIps;

    @Value("${app.capture.neutron.write.enabled:false}")
    private boolean captureWrites;
    @Value("${app.capture.neutron.write.networks:true}")
    private boolean captureWriteNetworks;
    @Value("${app.capture.neutron.write.security-groups:true}")
    private boolean captureWriteSecurityGroups;

    @Value("${app.capture.nova.servers:true}")
    private boolean captureNovaServers;
    @Value("${app.capture.nova.servers-detail:true}")
    private boolean captureNovaServersDetail;
    @Value("${app.capture.nova.keypairs:true}")
    private boolean captureNovaKeypairs;
    @Value("${app.capture.nova.limits:true}")
    private boolean captureNovaLimits;
    @Value("${app.capture.nova.write.enabled:false}")
    private boolean captureNovaWrites;
    @Value("${app.capture.nova.write.keypairs:true}")
    private boolean captureNovaWriteKeypairs;

    @Value("${app.capture.glance.images:true}")
    private boolean captureGlanceImages;
    @Value("${app.capture.glance.tasks:false}")
    private boolean captureGlanceTasks;

    @Value("${app.capture.cinder.volumes:true}")
    private boolean captureCinderVolumes;
    @Value("${app.capture.cinder.volumes-detail:true}")
    private boolean captureCinderVolumesDetail;
    @Value("${app.capture.cinder.snapshots:true}")
    private boolean captureCinderSnapshots;
    @Value("${app.capture.cinder.backups:true}")
    private boolean captureCinderBackups;
    @Value("${app.capture.cinder.limits:true}")
    private boolean captureCinderLimits;

    @Value("${app.capture.extra.nova.port:8774}")
    private int extraNovaPort;
    @Value("${app.capture.extra.glance.port:9292}")
    private int extraGlancePort;
    @Value("${app.capture.extra.cinder.port:8776}")
    private int extraCinderPort;
    @Value("${app.capture.extra.nova.uris:}")
    private List<String> extraNovaUris;
    @Value("${app.capture.extra.glance.uris:}")
    private List<String> extraGlanceUris;
    @Value("${app.capture.extra.cinder.uris:}")
    private List<String> extraCinderUris;

    @Override
    public void run(ApplicationArguments args) {
        if (!enabled) {
            log.info("Openstack capture disabled");
            return;
        }
        // Initial warm-up pause to avoid spike at startup
        rateLimiter.warmup();
        if (token == null || token.isBlank()) {
            token = tryIssueScopedToken(projectId);
            if (token == null || token.isBlank()) {
                log.warn("Openstack capture enabled but token is empty and login failed — skipping");
                log.warn("Provide app.capture.token or keystone credentials, and preferably app.capture.project-id for project-scoped token.");
                return;
            }
        }

        List<NeutronCaptureScenarios.Scenario> neutronScenarios = neutronCaptureScenarios.build(
                token, projectId, captureNetworks, capturePorts, captureSecurityGroups,
                captureSecurityGroupRules, captureSubnets, captureRouters, captureFloatingIps);

        neutronScenarios.forEach(this::executeAndStore);

        var novaScenarios = novaCaptureScenarios.build(token, projectId, captureNovaServers, captureNovaServersDetail, captureNovaKeypairs, captureNovaLimits);
        novaScenarios.forEach(this::executeAndStore);

        var glanceScenarios = glanceCaptureScenarios.build(token, captureGlanceImages, captureGlanceTasks);
        glanceScenarios.forEach(this::executeAndStore);

        var cinderScenarios = cinderCaptureScenarios.build(token, projectId, captureCinderVolumes, captureCinderVolumesDetail, captureCinderSnapshots, captureCinderBackups, captureCinderLimits);
        cinderScenarios.forEach(this::executeAndStore);

        // Extra URI-based GETs (config-driven)
        ScopeType[] order = computeScopeOrder();
        extraGetCaptureService.captureWithScope("nova", extraNovaPort, extraNovaUris, tokenManager, projectId, order);
        extraGetCaptureService.captureWithScope("glance", extraGlancePort, extraGlanceUris, tokenManager, projectId, order);
        extraGetCaptureService.captureWithScope("cinder", extraCinderPort, extraCinderUris, tokenManager, projectId, order);

        // Auto-follow-up based on captured lists (IDs)
        captureIndex.load();
        autoFollowUpCaptureService.run(token, projectId, captureIndex);
        // Full GET sweep across modules
        getSweepService.run(token, projectId, captureIndex);

        if (captureWrites) {
            log.info("Capture writes enabled. networks={}, security-groups={}", captureWriteNetworks, captureWriteSecurityGroups);
            if (captureWriteNetworks) {
                log.info("Starting Neutron network CRUD capture");
                neutronWriteCaptureService.captureNetworkCRUD(token, projectId);
            }
            if (captureWriteSecurityGroups) {
                log.info("Starting Neutron security group CRUD capture");
                neutronWriteCaptureService.captureSecurityGroupCRUD(token, projectId);
            }
        }

        if (captureNovaWrites) {
            log.info("Capture NOVA writes enabled. keypairs={}", captureNovaWriteKeypairs);
            if (captureNovaWriteKeypairs) {
                log.info("Starting Nova keypair CRUD capture");
                novaWriteCaptureService.captureKeypairCRUD(token);
            }
        }
    }

    private void executeAndStore(NeutronCaptureScenarios.Scenario s) {
        try {
            doGetWithScopeFallback(s.component(), s.name(), s.uri(), s.port(), s.query());
        } catch (Exception e) {
            log.warn("Capture failed for {} {}:{}", s.method(), s.uri(), s.port(), e);
        }
    }

    private void executeAndStore(NovaCaptureScenarios.Scenario s) {
        try {
            doGetWithScopeFallback(s.component(), s.name(), s.uri(), s.port(), s.query());
        } catch (Exception e) {
            log.warn("Capture failed for {} {}:{}", s.method(), s.uri(), s.port(), e);
        }
    }

    private void executeAndStore(GlanceCaptureScenarios.Scenario s) {
        try {
            doGetWithScopeFallback(s.component(), s.name(), s.uri(), s.port(), s.query());
        } catch (Exception e) {
            log.warn("Capture failed for {} {}:{}", s.method(), s.uri(), s.port(), e);
        }
    }

    private void executeAndStore(CinderCaptureScenarios.Scenario s) {
        try {
            doGetWithScopeFallback(s.component(), s.name(), s.uri(), s.port(), s.query());
        } catch (Exception e) {
            log.warn("Capture failed for {} {}:{}", s.method(), s.uri(), s.port(), e);
        }
    }

    private void doGetWithScopeFallback(String component, String name, String uri, int port, Map<String, String> query) {
        ScopeType[] order = computeScopeOrder();
        for (ScopeType scope : order) {
            rateLimiter.pause();
            String t = tokenManager.getToken(scope, projectId);
            if (t == null || t.isBlank()) continue;
            Map<String, String> headers = Map.of(
                    "X-Auth-Token", t,
                    "X-Auth-Token-Scope", scopeName(scope)
            );
            try {
                ResponseEntity<JsonNode> resp = null;
                int attempts = Math.max(0, rateLimiter.getRetries()) + 1;
                for (int i = 0; i < attempts; i++) {
                    try {
                        resp = openstackAPICallModule.callGetAPI(uri, headers, query, port);
                        break;
                    } catch (WebClientRequestException cre) {
                        // Connection issues: backoff and retry
                        if (i < attempts - 1) {
                            long backoff = rateLimiter.getBackoffMs() * (i + 1);
                            try { Thread.sleep(backoff); } catch (InterruptedException ie) { Thread.currentThread().interrupt(); }
                            continue;
                        }
                        throw cre;
                    }
                }
                JsonNode body = resp != null ? resp.getBody() : null;
                int status = resp != null && resp.getStatusCode() != null ? resp.getStatusCode().value() : -1;
                Map<String, String> storedHeaders = Map.of(
                        "X-Auth-Token", "__MASKED__",
                        "X-Auth-Token-Scope", scopeName(scope)
                );
                captureStorage.save(component, name, "GET", uri, port, storedHeaders, query, status, body);
                rateLimiter.maybeCooldown();
                return; // success or at least captured
            } catch (WebClientResponseException e) {
                if (e.getStatusCode().value() == 403) {
                    log.info("[Capture] 403 for {} {} using scope {}; trying next...", component, uri, scopeName(scope));
                    continue; // try next scope
                }
                // Non-403 error; still store minimal info
                Map<String, String> storedHeaders = Map.of(
                        "X-Auth-Token", "__MASKED__",
                        "X-Auth-Token-Scope", scopeName(scope)
                );
                captureStorage.save(component, name, "GET", uri, port, storedHeaders, query, e.getStatusCode().value(), null);
                rateLimiter.maybeCooldown();
                return;
            } catch (Exception e) {
                log.warn("[Capture] Request failed for {} {}:{} with scope {}: {}", component, uri, port, scopeName(scope), e.toString());
                // try next scope
            }
        }
        // If no scope succeeded, persist a stub to ensure directory and traceability
        log.warn("[Capture] All scopes failed for {} {}:{}", component, uri, port);
        Map<String, String> storedHeaders = Map.of(
                "X-Auth-Token", "__MASKED__",
                "X-Auth-Token-Scope", "none"
        );
        captureStorage.save(component, name, "GET", uri, port, storedHeaders, query, -1, null);
    }

    private ScopeType[] computeScopeOrder() {
        String pref = preferTokenScope == null ? "project" : preferTokenScope.trim().toLowerCase();
        return switch (pref) {
            case "system" -> new ScopeType[]{ScopeType.SYSTEM, ScopeType.ADMIN_PROJECT, ScopeType.PROJECT};
            case "admin-project" -> new ScopeType[]{ScopeType.ADMIN_PROJECT, ScopeType.SYSTEM, ScopeType.PROJECT};
            default -> new ScopeType[]{ScopeType.PROJECT, ScopeType.SYSTEM, ScopeType.ADMIN_PROJECT};
        };
    }

    private String scopeName(ScopeType s) {
        return switch (s) {
            case SYSTEM -> "system";
            case ADMIN_PROJECT -> "admin-project";
            default -> "project";
        };
    }

    private String tryIssueScopedToken(String targetProjectId) {
        try {
            if (username == null || username.isBlank() || password == null || password.isBlank()) {
                log.warn("Keystone credentials not provided for capture login");
                return null;
            }
            var req = new com.acc.local.dto.auth.KeystonePasswordLoginRequest(username, password, domain);
            String pref = preferTokenScope == null ? "project" : preferTokenScope.trim().toLowerCase();
            switch (pref) {
                case "system" -> {
                    var systemScoped = keystoneExternalAdapter.getAdminToken(req); // system scope
                    return systemScoped.token();
                }
                case "admin-project" -> {
                    var adminProjectScoped = keystoneExternalAdapter.getAdminTokenWithAdminProjectScope(req);
                    return adminProjectScoped.token();
                }
                default -> {
                    if (targetProjectId != null && !targetProjectId.isBlank()) {
                        var unscoped = keystoneExternalAdapter.getUnscopedToken(req);
                        var scoped = keystoneExternalAdapter.getScopedToken(targetProjectId, unscoped.token());
                        return scoped.token();
                    }
                    // If no project id, fallback to system scope
                    var systemScoped = keystoneExternalAdapter.getAdminToken(req);
                    return systemScoped.token();
                }
            }
        } catch (Exception e) {
            log.warn("Failed to issue scoped token for capture: {}", e.toString());
            return null;
        }
    }
}
