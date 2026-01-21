package com.acc.local.capture;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Component
@RequiredArgsConstructor
public class OpenstackSweepCaptureService {

    private final ExtraGetCaptureService extraGetCaptureService;
    private final TokenManager tokenManager;

    @Value("${app.capture.sweep.enabled:false}")
    private boolean enabled;

    @Value("${app.capture.prefer-token-scope:project}")
    private String preferTokenScope;

    @Value("${app.capture.sweep.nova:true}") private boolean sweepNova;
    @Value("${app.capture.sweep.glance:true}") private boolean sweepGlance;
    @Value("${app.capture.sweep.cinder:true}") private boolean sweepCinder;

    @Value("${app.capture.auto-follow.nova.servers-limit:10}") private int novaServersLimit;
    @Value("${app.capture.auto-follow.nova.flavors-limit:10}") private int novaFlavorsLimit;
    @Value("${app.capture.auto-follow.glance.images-limit:10}") private int glanceImagesLimit;
    @Value("${app.capture.auto-follow.cinder.volumes-limit:10}") private int cinderVolumesLimit;
    @Value("${app.capture.auto-follow.cinder.snapshots-limit:10}") private int cinderSnapshotsLimit;
    @Value("${app.capture.auto-follow.cinder.backups-limit:10}") private int cinderBackupsLimit;

    public void run(String token, String projectId, CaptureIndex index) {
        if (!enabled) return;
        log.info("[Capture] Starting unified OpenStack Sweep & Follow-up");

        ScopeType[] order = computeScopeOrder();

        if (sweepNova) {
            Set<String> uris = new LinkedHashSet<>();
            uris.addAll(List.of("/v2.1/servers", "/v2.1/servers/detail", "/v2.1/os-keypairs", "/v2.1/limits", "/v2.1/flavors", "/v2.1/flavors/detail", "/v2.1/os-simple-tenant-usage"));
            if (isNotBlank(projectId)) {
                uris.add("/v2.1/os-quota-sets/" + projectId);
                uris.add("/v2.1/os-simple-tenant-usage/" + projectId);
            }
            index.getNovaServerIds().stream().limit(novaServersLimit).forEach(id -> {
                uris.add("/v2.1/servers/" + id);
                uris.add("/v2.1/servers/" + id + "/ips");
                uris.add("/v2.1/servers/" + id + "/diagnostics");
            });
            index.getNovaFlavorIds().stream().limit(novaFlavorsLimit).forEach(id -> uris.add("/v2.1/flavors/" + id));
            extraGetCaptureService.captureWithScope("nova", 8774, uris.stream().toList(), tokenManager, projectId, order);
        }

        if (sweepGlance) {
            Set<String> uris = new LinkedHashSet<>();
            uris.add("/v2/images");
            index.getGlanceImageIds().stream().limit(glanceImagesLimit).forEach(id -> uris.add("/v2/images/" + id));
            extraGetCaptureService.captureWithScope("glance", 9292, uris.stream().toList(), tokenManager, null, order);
        }

        if (sweepCinder) {
            Set<String> uris = new LinkedHashSet<>();
            uris.addAll(List.of("/v3/volumes", "/v3/volumes/detail", "/v3/limits", "/v3/backups"));
            uris.add(isNotBlank(projectId) ? "/v3/" + projectId + "/snapshots" : "/v3/snapshots");
            index.getCinderVolumeIds().stream().limit(cinderVolumesLimit).forEach(id -> {
                uris.add("/v3/volumes/" + id);
                uris.add("/v3/volumes/" + id + "/metadata");
            });
            index.getCinderSnapshotIds().stream().limit(cinderSnapshotsLimit).forEach(id -> {
                uris.add(isNotBlank(projectId) ? "/v3/" + projectId + "/snapshots/" + id : "/v3/snapshots/" + id);
            });
            index.getCinderBackupIds().stream().limit(cinderBackupsLimit).forEach(id -> uris.add("/v3/backups/" + id));
            extraGetCaptureService.captureWithScope("cinder", 8776, uris.stream().toList(), tokenManager, projectId, order);
        }
    }

    private boolean isNotBlank(String s) { return s != null && !s.isBlank(); }

    private ScopeType[] computeScopeOrder() {
        String pref = preferTokenScope == null ? "project" : preferTokenScope.trim().toLowerCase();
        return switch (pref) {
            case "system" -> new ScopeType[]{ScopeType.SYSTEM, ScopeType.ADMIN_PROJECT, ScopeType.PROJECT};
            case "admin-project" -> new ScopeType[]{ScopeType.ADMIN_PROJECT, ScopeType.SYSTEM, ScopeType.PROJECT};
            default -> new ScopeType[]{ScopeType.PROJECT, ScopeType.SYSTEM, ScopeType.ADMIN_PROJECT};
        };
    }
}