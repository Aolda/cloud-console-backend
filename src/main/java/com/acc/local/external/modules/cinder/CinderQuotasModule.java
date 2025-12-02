package com.acc.local.external.modules.cinder;

import com.acc.local.external.dto.cinder.quota.UpdateQuotaClassRequest;
import com.acc.local.external.dto.cinder.quota.UpdateQuotaRequest;
import com.acc.local.external.modules.OpenstackAPICallModule;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class CinderQuotasModule extends CinderAPIUtil {

    private final OpenstackAPICallModule openstackAPICallModule;

    public ResponseEntity<JsonNode> getQuotaClassSet(String token, String quotaClassName) {
        String uri = "/v3/os-quota-class-sets/" + quotaClassName;
        return openstackAPICallModule.callGetAPI(uri, Collections.singletonMap("X-Auth-Token", token), Collections.emptyMap(), port);
    }

    public ResponseEntity<JsonNode> getQuotaSet(String token, String projectId) {
        String uri = "/v3/os-quota-sets/" + projectId + "?usage=True";
        return openstackAPICallModule.callGetAPI(uri, Collections.singletonMap("X-Auth-Token", token), Collections.emptyMap(), port);
    }

    public ResponseEntity<JsonNode> getQuotaUsage(String token, String projectId, boolean usage) {
        String uri = "/v3/os-quota-sets/" + projectId;
        Map<String, String> queryParams = new HashMap<>();
        if (usage) {
            queryParams.put("usage", "True");
        }
        return openstackAPICallModule.callGetAPI(uri, Collections.singletonMap("X-Auth-Token", token), queryParams, port);
    }

    public ResponseEntity<JsonNode> updateQuota(String token, String projectId, UpdateQuotaRequest request) {
        String uri = "/v3/os-quota-sets/" + projectId;
        return openstackAPICallModule.callPutAPI(uri, Collections.singletonMap("X-Auth-Token", token), request, port);
    }

    public ResponseEntity<JsonNode> getQuotaDefaults(String token, String projectId) {
        String uri = "/v3/os-quota-sets/" + projectId + "/defaults";
        return openstackAPICallModule.callGetAPI(uri, Collections.singletonMap("X-Auth-Token", token), Collections.emptyMap(), port);
    }

    public ResponseEntity<JsonNode> updateQuotaClass(String token, String quotaClassName, UpdateQuotaClassRequest request) {
        String uri = "/v3/os-quota-class-sets/" + quotaClassName;
        return openstackAPICallModule.callPutAPI(uri, Collections.singletonMap("X-Auth-Token", token), request, port);
    }

    public ResponseEntity<JsonNode> deleteQuotaSet(String token, String projectId) {
        String uri = "/v3/os-quota-sets/" + projectId;
        return openstackAPICallModule.callDeleteAPI(uri, Collections.singletonMap("X-Auth-Token", token), port);
    }
}
