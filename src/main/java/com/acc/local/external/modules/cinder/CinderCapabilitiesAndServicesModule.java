package com.acc.local.external.modules.cinder;

import com.acc.local.external.dto.cinder.service.ServiceRequest;
import com.acc.local.external.modules.OpenstackAPICallModule;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class CinderCapabilitiesAndServicesModule extends CinderAPIUtil {

    private final OpenstackAPICallModule openstackAPICallModule;

    public ResponseEntity<JsonNode> getBackendCapabilities(String token, String projectId, String hostname) {
        String uri = "/v3/" + projectId + "/capabilities/" + hostname;
        return openstackAPICallModule.callGetAPI(uri, Collections.singletonMap("X-Auth-Token", token), Collections.emptyMap(), port);
    }

    public ResponseEntity<JsonNode> listCinderServices(String token, String projectId, Map<String, String> queryParams) {
        String uri = "/v3/" + projectId + "/os-services";
        return openstackAPICallModule.callGetAPI(uri, Collections.singletonMap("X-Auth-Token", token), queryParams, port);
    }

    public ResponseEntity<JsonNode> disableService(String token, String projectId, ServiceRequest request) {
        String uri = "/v3/" + projectId + "/os-services/disable";
        return openstackAPICallModule.callPutAPI(uri, Collections.singletonMap("X-Auth-Token", token), request, port);
    }

    public ResponseEntity<JsonNode> disableServiceLogReason(String token, String projectId, ServiceRequest request) {
        String uri = "/v3/" + projectId + "/os-services/disable-log-reason";
        return openstackAPICallModule.callPutAPI(uri, Collections.singletonMap("X-Auth-Token", token), request, port);
    }

    public ResponseEntity<JsonNode> enableService(String token, String projectId, ServiceRequest request) {
        String uri = "/v3/" + projectId + "/os-services/enable";
        return openstackAPICallModule.callPutAPI(uri, Collections.singletonMap("X-Auth-Token", token), request, port);
    }

    public ResponseEntity<JsonNode> getServiceLog(String token, String projectId, ServiceRequest request) {
        String uri = "/v3/" + projectId + "/os-services/get-log";
        return openstackAPICallModule.callPutAPI(uri, Collections.singletonMap("X-Auth-Token", token), request, port);
    }

    public ResponseEntity<JsonNode> setServiceLog(String token, String projectId, ServiceRequest request) {
        String uri = "/v3/" + projectId + "/os-services/set-log";
        return openstackAPICallModule.callPutAPI(uri, Collections.singletonMap("X-Auth-Token", token), request, port);
    }

    public ResponseEntity<JsonNode> freezeHost(String token, String projectId, ServiceRequest request) {
        String uri = "/v3/" + projectId + "/os-services/freeze";
        return openstackAPICallModule.callPutAPI(uri, Collections.singletonMap("X-Auth-Token", token), request, port);
    }

    public ResponseEntity<JsonNode> thawHost(String token, String projectId, ServiceRequest request) {
        String uri = "/v3/" + projectId + "/os-services/thaw";
        return openstackAPICallModule.callPutAPI(uri, Collections.singletonMap("X-Auth-Token", token), request, port);
    }

    public ResponseEntity<JsonNode> failoverHost(String token, String projectId, ServiceRequest request) {
        String uri = "/v3/" + projectId + "/os-services/failover_host";
        return openstackAPICallModule.callPutAPI(uri, Collections.singletonMap("X-Auth-Token", token), request, port);
    }
}
