package com.acc.local.external.modules.neutron;

import com.acc.local.external.dto.neutron.quotas.UpdateQuotaRequest;
import com.acc.local.external.modules.OpenstackAPICallModule;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class NeutronQuotasAPIModule extends NeutronAPIUtil {

    private final OpenstackAPICallModule openstackAPICallModule;

    public ResponseEntity<JsonNode> listQuotas(String token, Map<String, String> queryParams) {
        String uri = "/v2.0/quotas";
        return openstackAPICallModule.callGetAPI(uri, Collections.singletonMap("X-Auth-Token", token), queryParams, port);
    }

    public ResponseEntity<JsonNode> showQuota(String token, String projectId, Map<String, String> queryParams) {
        String uri = "/v2.0/quotas/" + projectId;
        return openstackAPICallModule.callGetAPI(uri, Collections.singletonMap("X-Auth-Token", token), queryParams, port);
    }

    public ResponseEntity<JsonNode> updateQuota(String token, String projectId, UpdateQuotaRequest request) {
        String uri = "/v2.0/quotas/" + projectId;
        return openstackAPICallModule.callPutAPI(uri, Collections.singletonMap("X-Auth-Token", token), request, port);
    }

    public ResponseEntity<JsonNode> deleteQuota(String token, String projectId) {
        String uri = "/v2.0/quotas/" + projectId;
        return openstackAPICallModule.callDeleteAPI(uri, Collections.singletonMap("X-Auth-Token", token), port);
    }

    public ResponseEntity<JsonNode> showQuotaDetails(String token, String projectId, Map<String, String> queryParams) {
        String uri = "/v2.0/quotas/" + projectId + "/details";
        return openstackAPICallModule.callGetAPI(uri, Collections.singletonMap("X-Auth-Token", token), queryParams, port);
    }
}
