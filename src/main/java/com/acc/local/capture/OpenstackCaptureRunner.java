package com.acc.local.capture;

import com.acc.local.dto.auth.KeystonePasswordLoginRequest;
import com.acc.local.external.adapters.keystone.KeystoneAPIExternalAdapter;
import com.acc.local.external.modules.OpenstackAPICallModule;
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
    private final OpenstackAPICallModule openstackAPICallModule;
    private final TokenManager tokenManager;
    private final KeystoneAPIExternalAdapter keystoneExternalAdapter;
    private final NeutronWriteCaptureService neutronWriteCaptureService;
    private final NovaWriteCaptureService novaWriteCaptureService;
    private final CaptureIndex captureIndex;
    private final OpenstackSweepCaptureService openstackSweepCaptureService;
    private final CaptureRateLimiter rateLimiter;

    @Value("${app.capture.enabled:true}") private boolean enabled;
    @Value("${app.capture.token:}") private String token;
    @Value("${app.capture.keystone.username:}") private String username;
    @Value("${app.capture.keystone.password:}") private String password;
    @Value("${app.capture.keystone.domain:Default}") private String domain;
    @Value("${app.capture.project-id:}") private String projectId;
    @Value("${app.capture.prefer-token-scope:project}") private String preferTokenScope;

    // 활성화 플래그들 (중략: 기존과 동일)
    @Value("${app.capture.neutron.networks:true}") private boolean captureNetworks;
    @Value("${app.capture.neutron.write.enabled:false}") private boolean captureWrites;
    @Value("${app.capture.nova.servers:true}") private boolean captureNovaServers;
    @Value("${app.capture.nova.write.enabled:false}") private boolean captureNovaWrites;

    @Override
    public void run(ApplicationArguments args) {
        if (!enabled) return;
        rateLimiter.warmup();

        if (token == null || token.isBlank()) {
            token = tryIssueScopedToken(projectId);
            if (token == null || token.isBlank()) return;
        }

        // 1. 핵심 리스트 수집
        neutronCaptureScenarios.build(token, projectId, captureNetworks, true, true, true, true, true, true).forEach(this::executeAndStore);
        novaCaptureScenarios.build(token, projectId, true, true, true, true).forEach(this::executeAndStore);
        glanceCaptureScenarios.build(token, true, false).forEach(this::executeAndStore);
        cinderCaptureScenarios.build(token, projectId, true, true, true, true, true).forEach(this::executeAndStore);

        // 2. 통합 Sweep & Follow-up (ID 기반 상세 조회 + 권한 자동 전환)
        captureIndex.load();
        openstackSweepCaptureService.run(token, projectId, captureIndex);

        // 3. 쓰기 시나리오
        if (captureWrites) {
            neutronWriteCaptureService.captureNetworkCRUD(token, projectId);
            neutronWriteCaptureService.captureSecurityGroupCRUD(token, projectId);
        }
        if (captureNovaWrites) {
            novaWriteCaptureService.captureKeypairCRUD(token);
        }
    }

    private void executeAndStore(NeutronCaptureScenarios.Scenario s) { processRequest(s.component(), s.name(), s.uri(), s.port(), s.query()); }
    private void executeAndStore(NovaCaptureScenarios.Scenario s) { processRequest(s.component(), s.name(), s.uri(), s.port(), s.query()); }
    private void executeAndStore(GlanceCaptureScenarios.Scenario s) { processRequest(s.component(), s.name(), s.uri(), s.port(), s.query()); }
    private void executeAndStore(CinderCaptureScenarios.Scenario s) { processRequest(s.component(), s.name(), s.uri(), s.port(), s.query()); }

    private void processRequest(String comp, String name, String uri, int port, Map<String, String> query) {
        ScopeType[] order = computeScopeOrder();
        for (ScopeType scope : order) {
            rateLimiter.pause();
            String t = tokenManager.getToken(scope, projectId);
            if (t == null) continue;
            try {
                ResponseEntity<JsonNode> resp = openstackAPICallModule.callGetAPI(uri, Map.of("X-Auth-Token", t, "X-Auth-Token-Scope", scopeName(scope)), query, port);
                captureStorage.save(comp, name, "GET", uri, port, Map.of("X-Auth-Token", "__MASKED__", "X-Auth-Token-Scope", scopeName(scope)), query, resp.getStatusCode().value(), resp.getBody());
                rateLimiter.maybeCooldown();
                return;
            } catch (WebClientResponseException e) {
                if (e.getStatusCode().value() == 403) continue;
                captureStorage.save(comp, name, "GET", uri, port, Map.of("X-Auth-Token", "__MASKED__"), query, e.getStatusCode().value(), null);
                return;
            } catch (Exception e) { log.warn("Failed {} {}: {}", comp, uri, e.toString()); }
        }
    }

    private ScopeType[] computeScopeOrder() {
        String pref = preferTokenScope == null ? "project" : preferTokenScope.trim().toLowerCase();
        return switch (pref) {
            case "system" -> new ScopeType[]{ScopeType.SYSTEM, ScopeType.ADMIN_PROJECT, ScopeType.PROJECT};
            case "admin-project" -> new ScopeType[]{ScopeType.ADMIN_PROJECT, ScopeType.SYSTEM, ScopeType.PROJECT};
            default -> new ScopeType[]{ScopeType.PROJECT, ScopeType.SYSTEM, ScopeType.ADMIN_PROJECT};
        };
    }

    private String scopeName(ScopeType s) { return s == ScopeType.SYSTEM ? "system" : s == ScopeType.ADMIN_PROJECT ? "admin-project" : "project"; }

    private String tryIssueScopedToken(String targetProjectId) {
        try {
            var req = new KeystonePasswordLoginRequest(username, password, domain);
            return keystoneExternalAdapter.getAdminToken(req).token(); // 기본 fallback
        } catch (Exception e) { return null; }
    }
}