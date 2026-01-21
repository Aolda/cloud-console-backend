package com.acc.local.capture;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.LinkedHashSet;
import java.util.Set;

@Slf4j
@Component
@RequiredArgsConstructor
public class GetSweepService {

    private final ExtraGetCaptureService extraGetCaptureService;
    private final TokenManager tokenManager;

    @Value("${app.capture.sweep.enabled:false}")
    private boolean enabled;

    @Value("${app.capture.sweep.nova:true}")
    private boolean sweepNova;
    @Value("${app.capture.sweep.glance:true}")
    private boolean sweepGlance;
    @Value("${app.capture.sweep.cinder:true}")
    private boolean sweepCinder;

    @Value("${app.capture.prefer-token-scope:project}")
    private String preferTokenScope;

    // reuse auto-follow limits for per-id expansion
    @Value("${app.capture.auto-follow.nova.servers-limit:10}")
    private int novaServersLimit;
    @Value("${app.capture.auto-follow.nova.flavors-limit:10}")
    private int novaFlavorsLimit;
    @Value("${app.capture.auto-follow.glance.images-limit:10}")
    private int glanceImagesLimit;
    @Value("${app.capture.auto-follow.cinder.volumes-limit:10}")
    private int cinderVolumesLimit;
    @Value("${app.capture.auto-follow.cinder.snapshots-limit:10}")
    private int cinderSnapshotsLimit;
    @Value("${app.capture.auto-follow.cinder.backups-limit:10}")
    private int cinderBackupsLimit;

    public void run(String token, String projectId, CaptureIndex index) {
        if (!enabled) return;
        log.info("[Capture] GET sweep enabled — covering module GET endpoints");

        if (sweepNova) sweepNova(token, projectId, index);
        if (sweepGlance) sweepGlance(token, index);
        if (sweepCinder) sweepCinder(token, projectId, index);
    }

    private void sweepNova(String token, String projectId, CaptureIndex index) {
        Set<String> uris = new LinkedHashSet<>();
        // Lists
        uris.add("/v2.1/servers");
        uris.add("/v2.1/servers/detail");
        uris.add("/v2.1/os-keypairs");
        uris.add("/v2.1/limits");
        uris.add("/v2.1/flavors");
        uris.add("/v2.1/flavors/detail");
        uris.add("/v2.1/os-simple-tenant-usage");
        if (projectId != null && !projectId.isBlank()) {
            uris.add("/v2.1/os-quota-sets/" + projectId);
            uris.add("/v2.1/os-simple-tenant-usage/" + projectId);
        }
        // Per-id
        index.getNovaServerIds().stream().limit(novaServersLimit).forEach(id -> {
            uris.add("/v2.1/servers/" + id);
            uris.add("/v2.1/servers/" + id + "/ips");
            uris.add("/v2.1/servers/" + id + "/diagnostics");
        });
        index.getNovaFlavorIds().stream().limit(novaFlavorsLimit).forEach(id -> {
            uris.add("/v2.1/flavors/" + id);
        });
        extraGetCaptureService.captureWithScope("nova", 8774, uris.stream().toList(), tokenManager, projectId, computeScopeOrder());
    }

    private void sweepGlance(String token, CaptureIndex index) {
        Set<String> uris = new LinkedHashSet<>();
        uris.add("/v2/images");
        index.getGlanceImageIds().stream().limit(glanceImagesLimit).forEach(id -> uris.add("/v2/images/" + id));
        extraGetCaptureService.captureWithScope("glance", 9292, uris.stream().toList(), tokenManager, null, computeScopeOrder());
    }

    private void sweepCinder(String token, String projectId, CaptureIndex index) {
        Set<String> uris = new LinkedHashSet<>();
        uris.add("/v3/volumes");
        uris.add("/v3/volumes/detail");
        uris.add("/v3/limits");
        if (projectId != null && !projectId.isBlank()) {
            uris.add("/v3/" + projectId + "/snapshots");
        } else {
            uris.add("/v3/snapshots");
        }
        uris.add("/v3/backups");
        index.getCinderVolumeIds().stream().limit(cinderVolumesLimit).forEach(id -> {
            uris.add("/v3/volumes/" + id);
            uris.add("/v3/volumes/" + id + "/metadata");
        });
        index.getCinderSnapshotIds().stream().limit(cinderSnapshotsLimit).forEach(id -> {
            if (projectId != null && !projectId.isBlank()) uris.add("/v3/" + projectId + "/snapshots/" + id);
            else uris.add("/v3/snapshots/" + id);
        });
        index.getCinderBackupIds().stream().limit(cinderBackupsLimit).forEach(id -> uris.add("/v3/backups/" + id));
        extraGetCaptureService.captureWithScope("cinder", 8776, uris.stream().toList(), tokenManager, projectId, computeScopeOrder());
    }

    private ScopeType[] computeScopeOrder() {
        String pref = preferTokenScope == null ? "project" : preferTokenScope.trim().toLowerCase();
        return switch (pref) {
            case "system" -> new ScopeType[]{ScopeType.SYSTEM, ScopeType.ADMIN_PROJECT, ScopeType.PROJECT};
            case "admin-project" -> new ScopeType[]{ScopeType.ADMIN_PROJECT, ScopeType.SYSTEM, ScopeType.PROJECT};
            default -> new ScopeType[]{ScopeType.PROJECT, ScopeType.SYSTEM, ScopeType.ADMIN_PROJECT};
        };
    }
}
