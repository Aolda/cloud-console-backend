package com.acc.local.external.modules.nova;

import com.acc.local.external.modules.OpenstackAPICallModule;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class NovaUsageReportAPIModule extends NovaAPIUtil
{

    private final OpenstackAPICallModule openstackAPICallModule;

    // Get Server Usage
    public ResponseEntity<JsonNode> getServerUsage(String token, Map<String, String> queryParams) {
        String uri = "/v2.1/os-simple-tenant-usage/";
        return openstackAPICallModule.callGetAPI(uri, Collections.singletonMap("X-Auth-Token", token), queryParams, port);
    }

    public ResponseEntity<JsonNode> getServerUsage(String token) {
        return getServerUsage(token, Collections.emptyMap());
    }

    // Get Server Usage with Details
    public ResponseEntity<JsonNode> getServerUsageDetail(String token, String tenantId, Map<String, String> queryParams) {
        String uri = "/v2.1/os-simple-tenant-usage/" + tenantId;
        return openstackAPICallModule.callGetAPI(uri, Collections.singletonMap("X-Auth-Token", token), queryParams, port);
    }

    public ResponseEntity<JsonNode> getServerUsageDetail(String token, String tenantId) {
        return getServerUsageDetail(token, tenantId, Collections.emptyMap());
    }
}
