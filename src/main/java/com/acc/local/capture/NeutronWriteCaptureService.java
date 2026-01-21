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
public class NeutronWriteCaptureService {

    private final OpenstackAPICallModule openstackAPICallModule;
    private final CaptureStorage captureStorage;

    private static final int NEUTRON_PORT = 9696;

    public void captureNetworkCRUD(String token, String projectId) {
        log.info("[Capture] Neutron Network CRUD start (projectId={})", projectId);
        String baseName = nowSuffix("network");
        String uri = "/v2.0/networks";
        Map<String, String> headers = Map.of("X-Auth-Token", token);

        // Create
        Map<String, Object> createBody = new HashMap<>();
        Map<String, Object> network = new HashMap<>();
        network.put("name", baseName);
        network.put("admin_state_up", true);
        if (projectId != null && !projectId.isBlank()) {
            network.put("project_id", projectId);
        }
        createBody.put("network", network);

        ResponseEntity<JsonNode> created = null;
        try {
            created = openstackAPICallModule.callPostAPI(uri, headers, createBody, NEUTRON_PORT);
            captureStorage.save("neutron", "networks_create", "POST", uri, NEUTRON_PORT, mask(headers), Map.of(),
                    created.getStatusCode().value(), created.getBody());
        } catch (Exception e) {
            log.warn("[Capture] Network create failed: {}", e.toString());
            return;
        }

        String networkId = extract(created, "network", "id");
        log.info("[Capture] Network created id={}", networkId);
        if (networkId == null) return;

        // Update
        String updateUri = "/v2.0/networks/" + networkId;
        Map<String, Object> updateBody = new HashMap<>();
        Map<String, Object> update = new HashMap<>();
        update.put("description", "updated-by-capture");
        updateBody.put("network", update);
        try {
            ResponseEntity<JsonNode> updated = openstackAPICallModule.callPutAPI(updateUri, headers, updateBody, NEUTRON_PORT);
            captureStorage.save("neutron", "networks_update", "PUT", updateUri, NEUTRON_PORT, mask(headers), Map.of(),
                    updated.getStatusCode().value(), updated.getBody());
        } catch (Exception e) {
            log.warn("[Capture] Network update failed: {}", e.toString());
        }

        // Delete
        try {
            ResponseEntity<JsonNode> deleted = openstackAPICallModule.callDeleteAPI(updateUri, headers, NEUTRON_PORT);
            int status = deleted != null && deleted.getStatusCode() != null ? deleted.getStatusCode().value() : -1;
            captureStorage.save("neutron", "networks_delete", "DELETE", updateUri, NEUTRON_PORT, mask(headers), Map.of(),
                    status, null);
        } catch (Exception e) {
            log.warn("[Capture] Network delete failed: {}", e.toString());
        }
    }

    public void captureSecurityGroupCRUD(String token, String projectId) {
        String baseName = nowSuffix("sg");
        String uri = "/v2.0/security-groups";
        Map<String, String> headers = Map.of("X-Auth-Token", token);

        // Create
        Map<String, Object> createBody = new HashMap<>();
        Map<String, Object> sg = new HashMap<>();
        sg.put("name", baseName);
        sg.put("description", "capture-sg");
        if (projectId != null && !projectId.isBlank()) {
            sg.put("project_id", projectId);
        }
        createBody.put("security_group", sg);

        ResponseEntity<JsonNode> created = null;
        try {
            created = openstackAPICallModule.callPostAPI(uri, headers, createBody, NEUTRON_PORT);
            captureStorage.save("neutron", "security_groups_create", "POST", uri, NEUTRON_PORT, mask(headers), Map.of(),
                    created.getStatusCode().value(), created.getBody());
        } catch (Exception e) {
            log.warn("[Capture] Security group create failed: {}", e.toString());
            return;
        }

        String sgId = extract(created, "security_group", "id");
        log.info("[Capture] Security group created id={}", sgId);
        if (sgId == null) return;

        // Update
        String updateUri = "/v2.0/security-groups/" + sgId;
        Map<String, Object> updateBody = new HashMap<>();
        Map<String, Object> update = new HashMap<>();
        update.put("description", "updated-by-capture");
        updateBody.put("security_group", update);
        try {
            ResponseEntity<JsonNode> updated = openstackAPICallModule.callPutAPI(updateUri, headers, updateBody, NEUTRON_PORT);
            captureStorage.save("neutron", "security_groups_update", "PUT", updateUri, NEUTRON_PORT, mask(headers), Map.of(),
                    updated.getStatusCode().value(), updated.getBody());
        } catch (Exception e) {
            log.warn("[Capture] Security group update failed: {}", e.toString());
        }

        // Delete
        try {
            ResponseEntity<JsonNode> deleted = openstackAPICallModule.callDeleteAPI(updateUri, headers, NEUTRON_PORT);
            int status = deleted != null && deleted.getStatusCode() != null ? deleted.getStatusCode().value() : -1;
            captureStorage.save("neutron", "security_groups_delete", "DELETE", updateUri, NEUTRON_PORT, mask(headers), Map.of(),
                    status, null);
        } catch (Exception e) {
            log.warn("[Capture] Security group delete failed: {}", e.toString());
        }
    }

    private String nowSuffix(String prefix) {
        return prefix + "-capture-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
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
