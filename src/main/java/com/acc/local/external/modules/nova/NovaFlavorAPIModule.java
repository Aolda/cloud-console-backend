package com.acc.local.external.modules.nova;

import com.acc.local.external.dto.nova.flavor.CreateFlavorRequest;
import com.acc.local.external.modules.OpenstackAPICallModule;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class NovaFlavorAPIModule extends NovaAPIUtil {

    private final OpenstackAPICallModule openstackAPICallModule;

    public ResponseEntity<JsonNode> createFlavor(String token, String serverId, CreateFlavorRequest request) {
        String uri = "/v2.1/flavors";
        return openstackAPICallModule.callPostAPI(uri, Collections.singletonMap("X-Auth-Token", token), request, port);
    }

    // List Flavors
    public ResponseEntity<JsonNode> listFlavors(String token, Map<String, String> queryParams) {
        String uri = "/v2.1/flavors";
        return openstackAPICallModule.callGetAPI(uri, Collections.singletonMap("X-Auth-Token", token), queryParams, port);
    }

    // List Flavors without query parameters
    public ResponseEntity<JsonNode> listFlavors(String token) {
        return listFlavors(token, Collections.emptyMap());
    }

    // Get Flavor Details
    public ResponseEntity<JsonNode> listFlavorsDetails(String token, Map<String, String> queryParams) {
        String uri = "/v2.1/flavors/detail";
        return openstackAPICallModule.callGetAPI(uri, Collections.singletonMap("X-Auth-Token", token), queryParams, port);
    }

    // Get Flavor Details without query parameters
    public ResponseEntity<JsonNode> listFlavorsDetails(String token) {
        return listFlavorsDetails(token, Collections.emptyMap());
    }

    public ResponseEntity<JsonNode> showFlavorDetails(String token, String flavorId) {
        String uri = "/v2.1/flavors/" + flavorId;
        return openstackAPICallModule.callGetAPI(uri, Collections.singletonMap("X-Auth-Token", token), Collections.emptyMap(), port);
    }

    public ResponseEntity<JsonNode> deleteFlavor(String token, String serverId, String flavorId) {
        String uri = "/v2.1/flavors/" + flavorId;
        return openstackAPICallModule.callDeleteAPI(uri, Collections.singletonMap("X-Auth-Token", token), port);
    }
}
