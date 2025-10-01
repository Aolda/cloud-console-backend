package com.acc.local.external.modules.neutron;

import com.acc.local.external.dto.neutron.flavors.AssociateFlavorRequest;
import com.acc.local.external.dto.neutron.flavors.CreateFlavorRequest;
import com.acc.local.external.dto.neutron.flavors.UpdateFlavorRequest;
import com.acc.local.external.modules.OpenstackAPICallModule;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class NeutronFlavorsAPIModule extends NeutronAPIUtil {

    private final OpenstackAPICallModule openstackAPICallModule;

    public ResponseEntity<JsonNode> listFlavors(String token, Map<String, String> queryParams) {
        String uri = "/v2.0/flavors";
        return openstackAPICallModule.callGetAPI(uri, Collections.singletonMap("X-Auth-Token", token), queryParams, port);
    }

    public ResponseEntity<JsonNode> createFlavor(String token, CreateFlavorRequest request) {
        String uri = "/v2.0/flavors";
        return openstackAPICallModule.callPostAPI(uri, Collections.singletonMap("X-Auth-Token", token), request, port);
    }

    public ResponseEntity<JsonNode> showFlavor(String token, String flavorId) {
        String uri = "/v2.0/flavors/" + flavorId;
        return openstackAPICallModule.callGetAPI(uri, Collections.singletonMap("X-Auth-Token", token), Collections.emptyMap(), port);
    }

    public ResponseEntity<JsonNode> updateFlavor(String token, String flavorId, UpdateFlavorRequest request) {
        String uri = "/v2.0/flavors/" + flavorId;
        return openstackAPICallModule.callPutAPI(uri, Collections.singletonMap("X-Auth-Token", token), request, port);
    }

    public ResponseEntity<JsonNode> deleteFlavor(String token, String flavorId) {
        String uri = "/v2.0/flavors/" + flavorId;
        return openstackAPICallModule.callDeleteAPI(uri, Collections.singletonMap("X-Auth-Token", token), port);
    }

    public ResponseEntity<JsonNode> associateFlavorWithServiceProfile(String token, String flavorId, AssociateFlavorRequest request) {
        String uri = "/v2.0/flavors/" + flavorId + "/service_profiles";
        return openstackAPICallModule.callPostAPI(uri, Collections.singletonMap("X-Auth-Token", token), request, port);
    }

    public ResponseEntity<JsonNode> disassociateFlavorFromServiceProfile(String token, String flavorId, String profileId) {
        String uri = "/v2.0/flavors/" + flavorId + "/service_profiles/" + profileId;
        return openstackAPICallModule.callDeleteAPI(uri, Collections.singletonMap("X-Auth-Token", token), port);
    }
}