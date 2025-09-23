package com.acc.local.external.modules.cinder;

import com.acc.local.external.dto.cinder.defaultTypes.UpdateVolumeTypeRequest;
import com.acc.local.external.modules.OpenstackAPICallModule;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class CinderDefaultTypesModule extends CinderAPIUtil {

    private final OpenstackAPICallModule openstackAPICallModule;

    public ResponseEntity<JsonNode> getDefaultType(String token, String projectId) {
        String uri = "/v3/default-types/" + projectId;
        return openstackAPICallModule.callGetAPI(uri, Collections.singletonMap("X-Auth-Token", token), Collections.emptyMap(), port);
    }

    public ResponseEntity<JsonNode> updateVolumeType(String token, String projectId, UpdateVolumeTypeRequest request) {
        String uri = "/v3/default-types/" + projectId;
        return openstackAPICallModule.callPutAPI(uri, Collections.singletonMap("X-Auth-Token", token), request, port);
    }

    public ResponseEntity<JsonNode> listDefaultTypes(String token, Map<String, String> queryParams) {
        String uri = "/v3/default-types/";
        return openstackAPICallModule.callGetAPI(uri, Collections.singletonMap("X-Auth-Token", token), queryParams, port);
    }

    public ResponseEntity<JsonNode> deleteDefaultType(String token, String projectId) {
        String uri = "/v3/default-types/" + projectId;
        return openstackAPICallModule.callDeleteAPI(uri, Collections.singletonMap("X-Auth-Token", token), port);
    }
}
