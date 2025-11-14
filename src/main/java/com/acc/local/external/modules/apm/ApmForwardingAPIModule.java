package com.acc.local.external.modules.apm;

import com.acc.local.external.dto.apm.CreateForwardingRequest;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class ApmForwardingAPIModule {

    private final ApmAPIModule apmAPIModule;
    private final String uri = "/api/forwarding";

    public ResponseEntity<JsonNode> createForwarding(String token, String projectId, CreateForwardingRequest request) {
        return apmAPIModule.callPostAPI(uri,
                Collections.singletonMap("X-Subject-Token", token),
                Collections.singletonMap("projectId", projectId), request);
    }

    public ResponseEntity<JsonNode> deleteForwarding(String token, String forwardingId) {
        return apmAPIModule.callDeleteAPI(uri, Collections.singletonMap("X-Subject-Token", token),
                Collections.singletonMap("forwardingId", forwardingId));
    }

    public ResponseEntity<JsonNode> listForwarding(String token, Map<String, String> queryParams) {
        return apmAPIModule.callGetAPI(uri + 's', Collections.singletonMap("X-Subject-Token", token), queryParams);
    }
}
