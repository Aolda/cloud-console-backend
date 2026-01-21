package com.acc.local.capture;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class CaptureStorage {

    private static final String CAPTURE_DIR = "openstack-captures";
    private final ObjectMapper mapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);

    public void save(String component, String name, String method, String uri, int port,
                     Map<String, String> headers, Map<String, String> query, int status,
                     JsonNode body) {
        ensureDir(component);
        File dir = new File(CAPTURE_DIR, component);
        String baseName = sanitize(name);
        // Remove previous versions for this scenario
        File[] olds = dir.listFiles((d, fname) -> fname.startsWith(baseName + "_") || fname.equals(baseName + ".json"));
        if (olds != null) {
            for (File old : olds) {
                try { old.delete(); } catch (Exception ignore) {}
            }
        }
        File file = new File(dir, baseName + ".json");
        try (FileWriter fw = new FileWriter(file)) {
            fw.write("{\n");
            fw.write("  \"request\": {\n");
            fw.write("    \"method\": \"" + method + "\",\n");
            fw.write("    \"uri\": \"" + uri + "\",\n");
            fw.write("    \"port\": " + port + ",\n");
            if (headers != null && !headers.isEmpty()) {
                fw.write("    \"headers\": " + mapper.writeValueAsString(headers) + ",\n");
            }
            if (query != null && !query.isEmpty()) {
                fw.write("    \"query\": " + mapper.writeValueAsString(query) + "\n");
            } else {
                fw.write("    \"query\": {}\n");
            }
            fw.write("  },\n");
            fw.write("  \"response\": {\n");
            fw.write("    \"status\": " + status + ",\n");
            fw.write("    \"body\": " + (body == null ? "null" : mapper.writeValueAsString(body)) + "\n");
            fw.write("  }\n");
            fw.write("}\n");
            log.info("Captured (latest) {}:{} {} -> {}.json (status {})", component, port, uri, baseName, status);
        } catch (IOException e) {
            log.error("Failed to save capture {}:{} {}", component, port, uri, e);
        }
    }

    private void ensureDir(String component) {
        File base = new File(CAPTURE_DIR, component);
        if (!base.exists()) {
            base.mkdirs();
        }
    }

    private String sanitize(String s) {
        return s.replaceAll("[^a-zA-Z0-9._-]", "_");
    }
}
