package com.acc.local.capture;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Component
public class CaptureIndex {

    private static final String BASE_DIR = "openstack-captures";
    private final ObjectMapper mapper = new ObjectMapper();

    @Getter private final Set<String> novaServerIds = new HashSet<>();
    @Getter private final Set<String> novaFlavorIds = new HashSet<>();
    @Getter private final Set<String> glanceImageIds = new HashSet<>();
    @Getter private final Set<String> cinderVolumeIds = new HashSet<>();
    @Getter private final Set<String> cinderSnapshotIds = new HashSet<>();
    @Getter private final Set<String> cinderBackupIds = new HashSet<>();

    public void load() {
        novaServerIds.clear();
        novaFlavorIds.clear();
        glanceImageIds.clear();
        cinderVolumeIds.clear();
        cinderSnapshotIds.clear();
        cinderBackupIds.clear();

        loadNova();
        loadGlance();
        loadCinder();
    }

    private void loadNova() {
        File novaDir = new File(BASE_DIR, "nova");
        for (File f : listJson(novaDir)) {
            try {
                JsonNode root = mapper.readTree(f);
                JsonNode body = root.path("response").path("body");
                // servers
                var servers = body.path("servers");
                if (servers.isArray()) {
                    servers.forEach(n -> addText(n, "id", novaServerIds));
                }
                // flavors
                var flavors = body.path("flavors");
                if (flavors.isArray()) {
                    flavors.forEach(n -> addText(n, "id", novaFlavorIds));
                }
                // keypairs not used for follow-up standard GET here
            } catch (Exception e) {
                log.debug("Index parse failed (nova): {}", f.getName());
            }
        }
    }

    private void loadGlance() {
        File dir = new File(BASE_DIR, "glance");
        for (File f : listJson(dir)) {
            try {
                JsonNode root = mapper.readTree(f);
                JsonNode body = root.path("response").path("body");
                var images = body.path("images");
                if (images.isArray()) {
                    images.forEach(n -> addText(n, "id", glanceImageIds));
                }
            } catch (Exception e) {
                log.debug("Index parse failed (glance): {}", f.getName());
            }
        }
    }

    private void loadCinder() {
        File dir = new File(BASE_DIR, "cinder");
        for (File f : listJson(dir)) {
            try {
                JsonNode root = mapper.readTree(f);
                JsonNode body = root.path("response").path("body");
                var volumes = body.path("volumes");
                if (volumes.isArray()) {
                    volumes.forEach(n -> addText(n, "id", cinderVolumeIds));
                }
                var snapshots = body.path("snapshots");
                if (snapshots.isArray()) {
                    snapshots.forEach(n -> addText(n, "id", cinderSnapshotIds));
                }
                var backups = body.path("backups");
                if (backups.isArray()) {
                    backups.forEach(n -> addText(n, "id", cinderBackupIds));
                }
            } catch (Exception e) {
                log.debug("Index parse failed (cinder): {}", f.getName());
            }
        }
    }

    private List<File> listJson(File dir) {
        List<File> out = new ArrayList<>();
        if (!dir.exists() || !dir.isDirectory()) return out;
        File[] files = dir.listFiles((d, name) -> name.endsWith(".json"));
        if (files != null) {
            for (File f : files) {
                if (Files.isRegularFile(f.toPath())) out.add(f);
            }
        }
        return out;
    }

    private void addText(JsonNode node, String field, Set<String> target) {
        if (node.hasNonNull(field)) {
            target.add(node.get(field).asText());
        }
    }
}

