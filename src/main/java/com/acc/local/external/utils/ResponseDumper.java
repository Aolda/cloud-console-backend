package com.acc.local.external.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@Slf4j
@Component
public class ResponseDumper {

    private static final String DUMP_DIR = "response-dumps";
    private final ObjectMapper objectMapper;
    private final boolean dumpEnabled;

    public ResponseDumper(@Value("${app.response-dump.enabled:true}") boolean dumpEnabled) {
        this.dumpEnabled = dumpEnabled;
        this.objectMapper = new ObjectMapper();
        this.objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        if (dumpEnabled) {
            createDumpDirectory();
        }
    }

    private void createDumpDirectory() {
        File dir = new File(DUMP_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }

    public void dumpResponse(String httpMethod, String uri, int port, Map<String, String> headers,
                           JsonNode responseBody, int statusCode) {
        if (!dumpEnabled || responseBody == null) {
            return;
        }

        try {
            String component = resolveComponentByPort(port);
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss-SSS"));
            String fileName = String.format("%s_%s_%s_%s.json",
                component, sanitizeFileName(uri), httpMethod.toUpperCase(), timestamp);

            File file = new File(DUMP_DIR, fileName);

            try (FileWriter writer = new FileWriter(file)) {
                writer.write(String.format("// Component: %s\n", component));
                writer.write(String.format("// Method: %s\n", httpMethod.toUpperCase()));
                writer.write(String.format("// URI: %s\n", uri));
                writer.write(String.format("// Port: %d\n", port));
                writer.write(String.format("// Status Code: %d\n", statusCode));
                writer.write(String.format("// Timestamp: %s\n", LocalDateTime.now()));
                if (headers != null && headers.containsKey("X-Auth-Token")) {
                    writer.write("// Has Auth Token: Yes\n");
                }
                writer.write("// Response Body:\n");
                writer.write(objectMapper.writeValueAsString(responseBody));
            }

            log.info("Response dumped: {}", file.getName());

        } catch (IOException e) {
            log.error("Failed to dump response for URI: {}", uri, e);
        }
    }

    private String resolveComponentByPort(int port) {
        return switch (port) {
            case 5000 -> "keystone";
            case 8774 -> "nova";
            case 8776 -> "cinder";
            case 9292 -> "glance";
            case 9696 -> "neutron";
            default -> "unknown-" + port;
        };
    }

    private String sanitizeFileName(String fileName) {
        return fileName.replaceAll("[^a-zA-Z0-9._-]", "_");
    }
}