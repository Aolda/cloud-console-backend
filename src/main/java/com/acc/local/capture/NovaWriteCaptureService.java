package com.acc.local.capture;

import com.acc.local.external.modules.OpenstackAPICallModule;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class NovaWriteCaptureService {

    private final OpenstackAPICallModule openstackAPICallModule;
    private final CaptureStorage captureStorage;

    private static final int NOVA_PORT = 8774;

    public void captureKeypairCRUD(String token) {
        log.info("[Capture] Nova Keypair CRUD start");
        String baseName = nowSuffix("cap-key");
        String uri = "/v2.1/os-keypairs";
        Map<String, String> headers = Map.of("X-Auth-Token", token);

        // Create
        Map<String, Object> createBody = new HashMap<>();
        Map<String, Object> kp = new HashMap<>();
        kp.put("name", baseName);
        createBody.put("keypair", kp);
        ResponseEntity<JsonNode> created;
        try {
            created = openstackAPICallModule.callPostAPI(uri, headers, createBody, NOVA_PORT);
            captureStorage.save("nova", "keypairs_create", "POST", uri, NOVA_PORT, mask(headers), Map.of(),
                    created.getStatusCode().value(), created.getBody());
        } catch (Exception e) {
            log.warn("[Capture] Keypair create failed: {}", e.toString());
            return;
        }

        String name = extract(created, "keypair", "name");
        if (name == null) return;
        log.info("[Capture] Keypair created name={}", name);

        // Get
        String getUri = "/v2.1/os-keypairs/" + name;
        try {
            ResponseEntity<JsonNode> got = openstackAPICallModule.callGetAPI(getUri, headers, Map.of(), NOVA_PORT);
            captureStorage.save("nova", "keypairs_get", "GET", getUri, NOVA_PORT, mask(headers), Map.of(),
                    got.getStatusCode().value(), got.getBody());
        } catch (Exception e) {
            log.warn("[Capture] Keypair get failed: {}", e.toString());
        }

        // Delete
        try {
            ResponseEntity<JsonNode> deleted = openstackAPICallModule.callDeleteAPI(getUri, headers, NOVA_PORT);
            int status = deleted != null && deleted.getStatusCode() != null ? deleted.getStatusCode().value() : -1;
            captureStorage.save("nova", "keypairs_delete", "DELETE", getUri, NOVA_PORT, mask(headers), Map.of(),
                    status, null);
        } catch (Exception e) {
            log.warn("[Capture] Keypair delete failed: {}", e.toString());
        }
    }

    private String nowSuffix(String prefix) {
        return prefix + "-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
    }

    private Map<String, String> mask(Map<String, String> headers) {
        if (headers == null) return null;
        if (headers.containsKey("X-Auth-Token")) return Map.of("X-Auth-Token", "__MASKED__");
        return headers;
    }

    private String extract(ResponseEntity<JsonNode> resp, String rootKey, String idKey) {
        try {
            if (resp == null || resp.getBody() == null) return null;
            JsonNode root = resp.getBody().get(rootKey);
            if (root == null) return null;
            JsonNode idNode = root.get(idKey);
            return idNode != null ? idNode.asText() : null;
        } catch (Exception e) {
            return null;
        }
    }
}

