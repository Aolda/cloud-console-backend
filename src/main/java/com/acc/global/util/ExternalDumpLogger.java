package com.acc.global.util;

import com.acc.global.config.ExternalDumpProperties;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ExternalDumpLogger {
    private final ExternalDumpProperties props;
    private final ObjectMapper mapper;
    private final ObjectWriter pretty;
    private final Set<String> seen; // endpoint de-duplication per run

    public ExternalDumpLogger(ExternalDumpProperties props) {
        this.props = props;
        this.mapper = new ObjectMapper();
        this.pretty = mapper.writerWithDefaultPrettyPrinter();
        this.seen = ConcurrentHashMap.newKeySet();
    }

    public boolean isEnabled() {
        return props.isEnabled();
    }

    public void dump(String method,
                     String uri,
                     int port,
                     Map<String, String> headers,
                     Map<String, ?> queryParams,
                     Object requestBody,
                     ResponseEntity<JsonNode> response) {
        if (!isEnabled()) return;
        // de-duplicate: only first request per method+uri saved in a single run
        String key = method + "|" + uri;
        if (seen.contains(key)) return;
        seen.add(key);
        try {
            Path dir = Path.of(props.getDir());
            Files.createDirectories(dir);

            String safeUri = uri.replace('/', '_').replace('{', '_').replace('}', '_');
            String ts = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss_SSS"));
            String file = String.format("%s_%s_%d_%s.json", ts, method, port, safeUri);
            Path path = dir.resolve(file);

            ObjectNode root = mapper.createObjectNode();
            root.put("method", method);
            root.put("uri", uri);
            root.put("port", port);

            // headers (masked)
            Map<String, String> masked = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
            if (headers != null) masked.putAll(headers);
            maskHeader(masked, "Authorization");
            maskHeader(masked, "X-Auth-Token");
            maskHeader(masked, "X-Subject-Token");
            root.set("headers", mapper.valueToTree(masked));

            // query params
            if (queryParams != null) {
                root.set("queryParams", mapper.valueToTree(queryParams));
            }

            // request body
            if (requestBody != null) {
                root.set("requestBody", mapper.valueToTree(requestBody));
            }

            // response
            if (response != null) {
                ObjectNode resp = mapper.createObjectNode();
                resp.put("status", response.getStatusCodeValue());
                resp.set("body", response.getBody());
                root.set("response", resp);
            }

            byte[] out = pretty.writeValueAsBytes(root);
            Files.write(path, out, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE);
        } catch (IOException ignored) {
            // one-off 진단용이므로 실패는 무시
        }
    }

    private void maskHeader(Map<String, String> headers, String key) {
        if (headers == null) return;
        for (String k : new HashMap<>(headers).keySet()) {
            if (k.equalsIgnoreCase(key)) {
                headers.put(k, "***masked***");
            }
        }
    }
}
