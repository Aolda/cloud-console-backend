package com.acc.local.external.modules.neutron;

import com.acc.local.external.dto.neutron.networks.BulkCreateNetworkRequest;
import com.acc.local.external.dto.neutron.networks.CreateNetworkRequest;
import com.acc.local.external.dto.neutron.networks.UpdateNetworkRequest;
import com.acc.local.external.modules.OpenstackAPICallModule;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class NeutronNetworksAPIModule extends NeutronAPIUtil {

    private final OpenstackAPICallModule openstackAPICallModule;

    public ResponseEntity<JsonNode> listNeutronNetworks(String token, Map<String, String> queryParams) {
        String uri = "/v2.0/networks";
        return openstackAPICallModule.callGetAPI(uri, Collections.singletonMap("X-Auth-Token", token), queryParams, port);
    }

    public ResponseEntity<JsonNode> createNeutronNetwork(String token, CreateNetworkRequest request) {
        String uri = "/v2.0/networks";
        return openstackAPICallModule.callPostAPI(uri, Collections.singletonMap("X-Auth-Token", token), request, port);
    }

    public ResponseEntity<JsonNode> bulkCreateNeutronNetworks(String token, BulkCreateNetworkRequest request) {
        String uri = "/v2.0/networks";
        return openstackAPICallModule.callPostAPI(uri, Collections.singletonMap("X-Auth-Token", token), request, port);
    }

    public ResponseEntity<JsonNode> showNeutronNetwork(String token, String networkId) {
        String uri = "/v2.0/networks/" + networkId;
        return openstackAPICallModule.callGetAPI(uri, Collections.singletonMap("X-Auth-Token", token), Collections.emptyMap(), port);
    }

    public ResponseEntity<JsonNode> updateNeutronNetwork(String token, String networkId, UpdateNetworkRequest request) {
        String uri = "/v2.0/networks/" + networkId;
        return openstackAPICallModule.callPutAPI(uri, Collections.singletonMap("X-Auth-Token", token), request, port);
    }

    public ResponseEntity<JsonNode> deleteNeutronNetwork(String token, String networkId) {
        String uri = "/v2.0/networks/" + networkId;
        return openstackAPICallModule.callDeleteAPI(uri, Collections.singletonMap("X-Auth-Token", token), port);
    }
}