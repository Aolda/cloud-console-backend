package com.acc.local.capture;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class AutoFollowUpCaptureService {

    private final ExtraGetCaptureService extraGetCaptureService;

    @Value("${app.capture.auto-follow.enabled:false}")
    private boolean enabled;

    @Value("${app.capture.auto-follow.nova.servers-limit:5}")
    private int novaServersLimit;
    @Value("${app.capture.auto-follow.nova.flavors-limit:5}")
    private int novaFlavorsLimit;
    @Value("${app.capture.auto-follow.glance.images-limit:5}")
    private int glanceImagesLimit;
    @Value("${app.capture.auto-follow.cinder.volumes-limit:5}")
    private int cinderVolumesLimit;
    @Value("${app.capture.auto-follow.cinder.snapshots-limit:5}")
    private int cinderSnapshotsLimit;
    @Value("${app.capture.auto-follow.cinder.backups-limit:5}")
    private int cinderBackupsLimit;

    public void run(String token, String projectId, CaptureIndex index) {
        if (!enabled) return;
        log.info("[Capture] Auto-follow enabled: building follow-up GETs");

        // Nova follow-ups
        List<String> novaUris = new ArrayList<>();
        index.getNovaServerIds().stream().limit(novaServersLimit).forEach(id -> {
            novaUris.add("/v2.1/servers/" + id);
            novaUris.add("/v2.1/servers/" + id + "/ips");
            novaUris.add("/v2.1/servers/" + id + "/diagnostics");
        });
        index.getNovaFlavorIds().stream().limit(novaFlavorsLimit).forEach(id -> {
            novaUris.add("/v2.1/flavors/" + id);
        });
        extraGetCaptureService.capture("nova", 8774, token, novaUris);

        // Glance follow-ups
        List<String> glanceUris = new ArrayList<>();
        index.getGlanceImageIds().stream().limit(glanceImagesLimit).forEach(id -> {
            glanceUris.add("/v2/images/" + id);
        });
        extraGetCaptureService.capture("glance", 9292, token, glanceUris);

        // Cinder follow-ups
        List<String> cinderUris = new ArrayList<>();
        index.getCinderVolumeIds().stream().limit(cinderVolumesLimit).forEach(id -> {
            cinderUris.add("/v3/volumes/" + id);
        });
        index.getCinderSnapshotIds().stream().limit(cinderSnapshotsLimit).forEach(id -> {
            if (projectId != null && !projectId.isBlank()) {
                cinderUris.add("/v3/" + projectId + "/snapshots/" + id);
            } else {
                cinderUris.add("/v3/snapshots/" + id);
            }
        });
        index.getCinderBackupIds().stream().limit(cinderBackupsLimit).forEach(id -> {
            cinderUris.add("/v3/backups/" + id);
        });
        extraGetCaptureService.capture("cinder", 8776, token, cinderUris);
    }
}

